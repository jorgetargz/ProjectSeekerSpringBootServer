package com.jorgetargz.projectseekerspringboot.controllers.rest;

import com.google.firebase.auth.*;
import com.jorgetargz.projectseekerspringboot.config.properties.SecurityProperties;
import com.jorgetargz.projectseekerspringboot.dto.user.CreateUserDTO;
import com.jorgetargz.projectseekerspringboot.exceptions.UnauthorizedException;
import com.jorgetargz.projectseekerspringboot.security.Credentials;
import com.jorgetargz.projectseekerspringboot.services.FirebaseAuthService;
import com.jorgetargz.projectseekerspringboot.services.SecurityService;
import com.jorgetargz.projectseekerspringboot.services.UserService;
import com.jorgetargz.projectseekerspringboot.utils.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@RestController
@RequestMapping("api/session")
@Tag(name = "Session Endpoints", description = "Manage session")
public class SessionController {

    private final SecurityService securityService;
    private final UserService userService;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseAuthService firebaseAuthService;

    private final CookieUtils cookieUtils;

    private final SecurityProperties secProps;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final HttpServletResponse response;
    private final HttpServletRequest request;

    @Autowired
    public SessionController(SecurityService securityService, UserService userService, FirebaseAuth firebaseAuth, FirebaseAuthService firebaseAuthService, CookieUtils cookieUtils, SecurityProperties secProps, AuthenticationEntryPoint authenticationEntryPoint, HttpServletResponse response, HttpServletRequest request) {
        this.securityService = securityService;
        this.userService = userService;
        this.firebaseAuth = firebaseAuth;
        this.firebaseAuthService = firebaseAuthService;
        this.cookieUtils = cookieUtils;
        this.secProps = secProps;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.response = response;
        this.request = request;
    }

    @PostMapping("login")
    @Operation(summary = "Login user with firebase token and create session cookie")
    public void sessionLogin() {
        FirebaseToken decodedToken;
        Credentials.CredentialType type;
        UserRecord firebaseUser;
        String idToken = securityService.getBearerToken(request);
        try {
            decodedToken = firebaseAuth.verifyIdToken(idToken);
            type = Credentials.CredentialType.ID_TOKEN;
            firebaseUser = securityService.getUser(decodedToken);
            createUserIfNew(firebaseUser);
            List<GrantedAuthority> authorities = securityService.handleRoles(firebaseUser, decodedToken);
            setSessionCookie(idToken);
            securityService.setSecurityContext(firebaseUser, authorities, type, decodedToken);
        } catch (FirebaseAuthException e) {
            request.setAttribute("firebase-error", e.getAuthErrorCode().toString());
            try {
                authenticationEntryPoint.commence(request, response, null);
            } catch (IOException | ServletException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void createUserIfNew(UserRecord firebaseUser) {
        if (!userService.existsByFirebaseId(firebaseUser.getUid())) {
            CreateUserDTO createUserDTO = new CreateUserDTO();
            createUserDTO.setFirebaseId(firebaseUser.getUid());
            createUserDTO.setEmail(firebaseUser.getEmail());
            createUserDTO.setPhone(firebaseUser.getPhoneNumber());
            createUserDTO.setName(firebaseUser.getDisplayName());
            createUserDTO.setActiveRole(secProps.getValidApplicationRoles().getFreelancer());
            userService.createUser(createUserDTO);
            firebaseAuthService.addRole(firebaseUser.getUid(), createUserDTO.getActiveRole());
        }
    }

    private void setSessionCookie(String idToken) {
        long sessionExpiryMillis = TimeUnit.MINUTES.toMillis(secProps.getCookieProps().getMaxAgeInMinutes());
        SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(sessionExpiryMillis).build();
        try {
            String sessionCookieValue = FirebaseAuth.getInstance().createSessionCookie(idToken, options);
            cookieUtils.setCookie("session", sessionCookieValue);
        } catch (FirebaseAuthException e) {
            request.setAttribute("firebase-error", e.getMessage());
            throw new UnauthorizedException("Invalid Firebase token");
        }
    }

    @PostMapping("logout")
    @Operation(summary = "Logout user and delete session cookie")
    public void sessionLogout() {
        if (securityService.getCredentials().getType() == Credentials.CredentialType.SESSION) {
            cookieUtils.deleteCookie("session");
        }
    }

    @PostMapping("logout-everywhere")
    @Operation(summary = "Logout user everywhere and delete session cookie")
    public void sessionLogoutEverywhere() {
        if (securityService.getCredentials().getType() == Credentials.CredentialType.SESSION) {
            try {
                firebaseAuth.revokeRefreshTokens(securityService.getUser().getUid());
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
            }
            cookieUtils.deleteCookie("session");
        }
    }

}