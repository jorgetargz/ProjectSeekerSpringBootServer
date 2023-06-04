package com.jorgetargz.projectseekerspringboot.filters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.jorgetargz.projectseekerspringboot.config.properties.SecurityProperties;
import com.jorgetargz.projectseekerspringboot.security.Credentials;
import com.jorgetargz.projectseekerspringboot.services.SecurityService;
import com.jorgetargz.projectseekerspringboot.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Log4j2
public class SecurityFilter extends OncePerRequestFilter {

    private final SecurityService securityService;

    private final CookieUtils cookieUtils;

    private final SecurityProperties securityProps;

    private final FirebaseAuth firebaseAuth;

    @Autowired
    public SecurityFilter(SecurityService securityService, CookieUtils cookieUtils, SecurityProperties securityProps, FirebaseAuth firebaseAuth) {
        this.securityService = securityService;
        this.cookieUtils = cookieUtils;
        this.securityProps = securityProps;
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        authorize(request);
        filterChain.doFilter(request, response);
    }

    private void authorize(HttpServletRequest request) {
        FirebaseToken decodedToken = null;
        Credentials.CredentialType type = null;
        Cookie sessionCookie = cookieUtils.getCookie("session");
        try {
            // Handle session cookie
            if (sessionCookie != null) {
                String sessionCookieValue = sessionCookie.getValue();
                decodedToken = firebaseAuth.verifySessionCookie(sessionCookieValue,
                        securityProps.getFirebaseProps().isEnableCheckSessionRevoked());
                type = Credentials.CredentialType.SESSION;
            }

            // Handle firebase user to be used in security context as principal
            UserRecord firebaseUser = securityService.getUser(decodedToken);
            if (firebaseUser != null) {
                List<GrantedAuthority> authorities = securityService.handleRoles(firebaseUser, decodedToken);
                securityService.setSecurityContext(firebaseUser, authorities, type, decodedToken);
            }
        } catch (FirebaseAuthException e) {
            log.error("Firebase Exception", e);
            request.setAttribute("firebase-error", e.getAuthErrorCode().toString());
        }
    }
}