package com.jorgetargz.projectseekerspringboot.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.jorgetargz.projectseekerspringboot.security.Credentials;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


/**
 * This interface is used to get the user and credentials from the security context
 */
public interface SecurityService {
    /**
     * Gets the user from the security context
     *
     * @return The user with firebase data
     */
    UserRecord getUser();

    /**
     * Gets the user from the firebase token
     *
     * @param decodedToken The decoded token from the request
     * @return The user with firebase data
     * @throws FirebaseAuthException If the token is invalid
     */
    UserRecord getUser(FirebaseToken decodedToken) throws FirebaseAuthException;

    /**
     * Gets the roles from the user
     *
     * @param firebaseUser The user with firebase data
     * @param decodedToken The decoded token from the request
     * @return The roles of the user
     */
    List<GrantedAuthority> handleRoles(UserRecord firebaseUser, FirebaseToken decodedToken);

    /**
     * Sets the security context
     *
     * @param firebaseUser The user with firebase data
     * @param authorities  The roles of the user
     * @param type         The type of the credentials
     * @param decodedToken The decoded token from the request
     */
    void setSecurityContext(
            UserRecord firebaseUser,
            List<GrantedAuthority> authorities,
            Credentials.CredentialType type,
            FirebaseToken decodedToken);

    /**
     * Gets the credentials from the security context
     *
     * @return Credentials of the user
     */
    Credentials getCredentials();

    /**
     * Gets the bearer token from the request
     *
     * @param request The http request to get the bearer token from
     * @return The bearer token
     */
    String getBearerToken(HttpServletRequest request);
}
