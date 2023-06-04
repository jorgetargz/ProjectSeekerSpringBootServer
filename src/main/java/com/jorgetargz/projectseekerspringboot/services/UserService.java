package com.jorgetargz.projectseekerspringboot.services;

import com.jorgetargz.projectseekerspringboot.data.collections.user.User;
import com.jorgetargz.projectseekerspringboot.dto.user.*;
import org.bson.types.ObjectId;

public interface UserService {

    ProfileDTO getProfileByFirebaseId(String firebaseUID);

    ProfileDTO getProfileByUserId(String userID);

    void createUser(CreateUserDTO createUserDTO);

    void deleteUser(String firebaseUID);

    void changeRole(ChangeUserRoleDTO changeUserRoleDTO, String firebaseUID);

    void modifyFreelancerProfile(ModifyFreelancerProfileDTO modifyFreelancerProfileDTO, String firebaseUID);

    void modifyClientProfile(ModifyClientProfileDTO modifyClientProfileDTO, String firebaseUID);

    void addOrUpdateDeviceFCMToken(AddDeviceDTO addDeviceDTO, String firebaseUID);

    boolean existsByFirebaseId(String uid);

    User getUserByFirebaseId(String firebaseUID);

    User getUserByUserId(String userID);

    User getUserByUserId(ObjectId userID);
}
