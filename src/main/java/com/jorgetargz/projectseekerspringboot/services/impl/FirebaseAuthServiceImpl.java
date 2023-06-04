package com.jorgetargz.projectseekerspringboot.services.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.jorgetargz.projectseekerspringboot.config.properties.AppRolesProperties;
import com.jorgetargz.projectseekerspringboot.exceptions.BadRequestException;
import com.jorgetargz.projectseekerspringboot.services.FirebaseAuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Firebase Auth Service Implementation class
 */
@Service
@Log4j2
public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;

    private final AppRolesProperties rolesProperties;

    /**
     * Constructor for FirebaseAuth Service Implementation
     *
     * @param firebaseAuth
     * @param rolesProperties
     */
    @Autowired
    public FirebaseAuthServiceImpl(FirebaseAuth firebaseAuth, AppRolesProperties rolesProperties) {
        this.firebaseAuth = firebaseAuth;
        this.rolesProperties = rolesProperties;
    }


    @Override
    public void addRole(String uid, String role) {
        try {
            UserRecord user = firebaseAuth.getUser(uid);
            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
            if (rolesProperties.isRoleAvailable(role)) {
                claims.put(role, true);
                firebaseAuth.setCustomUserClaims(uid, claims);
            } else {
                throw new BadRequestException("Not a valid Application role, Allowed roles => "
                        + rolesProperties.getAllRoles());
            }

        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }

    }

    @Override
    public void removeRole(String uid, String role) {
        try {
            UserRecord user = firebaseAuth.getUser(uid);
            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
            claims.remove(role);
            firebaseAuth.setCustomUserClaims(uid, claims);
        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }
    }

    @Override
    public void changeActiveRole(String firebaseUID, String role) {
        try {
            UserRecord user = firebaseAuth.getUser(firebaseUID);
            Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
            if (role.equals(rolesProperties.getFreelancer())) {
                claims.remove(rolesProperties.getClient());
                claims.put(rolesProperties.getFreelancer(), true);
            } else if (role.equals(rolesProperties.getClient())) {
                claims.remove(rolesProperties.getFreelancer());
                claims.put(rolesProperties.getClient(), true);
            }
            firebaseAuth.setCustomUserClaims(firebaseUID, claims);
        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }
    }

    @Override
    public void deleteAccount(String firebaseUID) {
        try {
            firebaseAuth.deleteUser(firebaseUID);
        } catch (FirebaseAuthException e) {
            log.error("Firebase Auth Error ", e);
        }
    }
}
