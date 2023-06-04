package com.jorgetargz.projectseekerspringboot.services.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.jorgetargz.projectseekerspringboot.config.properties.SecurityProperties;
import com.jorgetargz.projectseekerspringboot.security.Credentials;
import com.jorgetargz.projectseekerspringboot.services.FirebaseAuthService;
import com.jorgetargz.projectseekerspringboot.services.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to get the user and credentials from the security context
 */
@Service
@Log4j2
public class SecurityServiceImpl implements SecurityService {

    private final SecurityProperties securityProps;
    private final FirebaseAuthService firebaseAuthService;
    private final HttpServletRequest request;

    @Autowired
    public SecurityServiceImpl(SecurityProperties securityProps, FirebaseAuthService firebaseAuthService, HttpServletRequest request) {
        this.securityProps = securityProps;
        this.firebaseAuthService = firebaseAuthService;
        this.request = request;
    }

    @Override
    public UserRecord getUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() == null) return null;
        return (UserRecord) securityContext.getAuthentication().getPrincipal();
    }

    @Override
    public UserRecord getUser(FirebaseToken decodedToken) throws FirebaseAuthException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        UserRecord firebaseUser = null;
        if (decodedToken != null) {
            firebaseUser = firebaseAuth.getUser(decodedToken.getUid());
        }
        return firebaseUser;
    }

    @Override
    public List<GrantedAuthority> handleRoles(UserRecord firebaseUser, FirebaseToken decodedToken) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (firebaseUser != null) {
            handleSuperAdminRole(firebaseUser, decodedToken, authorities);
            firebaseUser.getCustomClaims().forEach((k, v) -> authorities.add(new SimpleGrantedAuthority(k)));
        }
        return authorities;
    }

    private void handleSuperAdminRole(UserRecord firebaseUser, FirebaseToken decodedToken, List<GrantedAuthority> authorities) {
        if (securityProps.getSuperAdmins().contains(firebaseUser.getEmail())) {
            String superAdminRole = securityProps.getValidApplicationRoles().getSuperAdmin();
            if (!decodedToken.getClaims().containsKey(superAdminRole)) {
                try {
                    firebaseAuthService.addRole(decodedToken.getUid(), superAdminRole);
                } catch (Exception e) {
                    log.error("Super Role registeration expcetion ", e);
                }
            }
            authorities.add(new SimpleGrantedAuthority(superAdminRole));
        }
    }

    @Override
    public void setSecurityContext(
            UserRecord firebaseUser,
            List<GrantedAuthority> authorities,
            Credentials.CredentialType type,
            FirebaseToken decodedToken) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(firebaseUser,
                new Credentials(type, decodedToken), authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public Credentials getCredentials() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() == null) return null;
        return (Credentials) securityContext.getAuthentication().getCredentials();
    }

    @Override
    public String getBearerToken(HttpServletRequest request) {
        String bearerToken = null;
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            bearerToken = authorization.substring(7);
        }
        return bearerToken;
    }

}
