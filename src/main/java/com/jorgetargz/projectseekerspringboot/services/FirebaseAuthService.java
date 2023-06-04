package com.jorgetargz.projectseekerspringboot.services;

/**
 * FirebaseAuthService is an interface that defines the methods that will be used in the FirebaseAuthServiceImplementation class.
 */
public interface FirebaseAuthService {

    /**
     * Add role to user
     *
     * @param uid  user UID to add role to
     * @param role role to add
     * @throws Exception if role is not valid or user does not exist in Firebase
     */
    void addRole(String uid, String role);


    /**
     * Remove role from user
     *
     * @param uid  user UID to remove role from
     * @param role role to remove
     */
    void removeRole(String uid, String role);


    /**
     * Change active role of user
     *
     * @param firebaseUID user UID to change active role from
     * @param newRole     new active role
     */
    void changeActiveRole(String firebaseUID, String newRole);


    /**
     * Delete user from Firebase
     *
     * @param firebaseUID user UID to delete
     */
    void deleteAccount(String firebaseUID);
}
