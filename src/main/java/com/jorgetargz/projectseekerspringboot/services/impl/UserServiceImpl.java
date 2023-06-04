package com.jorgetargz.projectseekerspringboot.services.impl;

import com.jorgetargz.projectseekerspringboot.config.properties.AppRolesProperties;
import com.jorgetargz.projectseekerspringboot.data.UserRepository;
import com.jorgetargz.projectseekerspringboot.data.collections.user.ClientProfile;
import com.jorgetargz.projectseekerspringboot.data.collections.user.FreelancerProfile;
import com.jorgetargz.projectseekerspringboot.data.collections.user.User;
import com.jorgetargz.projectseekerspringboot.dto.user.*;
import com.jorgetargz.projectseekerspringboot.exceptions.NotFoundException;
import com.jorgetargz.projectseekerspringboot.services.FirebaseAuthService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements com.jorgetargz.projectseekerspringboot.services.UserService {

    private final UserRepository userRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final AppRolesProperties appRolesProperties;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FirebaseAuthService firebaseAuthService, AppRolesProperties appRolesProperties) {
        this.userRepository = userRepository;
        this.firebaseAuthService = firebaseAuthService;
        this.appRolesProperties = appRolesProperties;
    }

    @Override
    public ProfileDTO getProfileByFirebaseId(String firebaseUID) {
        User user = getUserByFirebaseId(firebaseUID);
        ProfileDTO profileDTO = new ProfileDTO();
        mapUserToProfileDTO(user, profileDTO);
        return profileDTO;
    }

    private void mapUserToProfileDTO(User user, ProfileDTO profileDTO) {
        profileDTO.setId(user.get_id().toHexString());
        profileDTO.setFirebaseId(user.getFirebaseId());
        profileDTO.setName(user.getName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setActiveRole(user.getActiveRole());
        if (user.getActiveRole().equals(appRolesProperties.getFreelancer())) {
            profileDTO.setSkills(user.getFreelancerProfile().getSkills());
            profileDTO.setTitle(user.getFreelancerProfile().getTitle());
            profileDTO.setAvailability(user.getFreelancerProfile().getAvailability());
            profileDTO.setDescription(user.getFreelancerProfile().getDescription());
        } else if (user.getActiveRole().equals(appRolesProperties.getClient())) {
            profileDTO.setTitle(user.getClientProfile().getTitle());
            profileDTO.setDescription(user.getClientProfile().getDescription());
        }
    }

    @Override
    public ProfileDTO getProfileByUserId(String userID) {
        User user = getUserByUserId(userID);
        ProfileDTO profileDTO = new ProfileDTO();
        mapUserToProfileDTO(user, profileDTO);
        return profileDTO;
    }

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setFirebaseId(createUserDTO.getFirebaseId());
        user.setName(createUserDTO.getName());
        user.setEmail(createUserDTO.getEmail());
        user.setPhone(createUserDTO.getPhone());
        user.setActiveRole(createUserDTO.getActiveRole());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String firebaseUID) {
        User user = getUserByFirebaseId(firebaseUID);
        ObjectId userId = user.get_id();
        userRepository.deleteById(userId);
        firebaseAuthService.deleteAccount(firebaseUID);
    }

    @Override
    public void changeRole(ChangeUserRoleDTO changeUserRoleDTO, String firebaseUID) {
        String newRole = changeUserRoleDTO.getActiveRole();
        firebaseAuthService.changeActiveRole(firebaseUID, newRole);
        User user = getUserByFirebaseId(firebaseUID);
        user.setActiveRole(newRole);
        userRepository.save(user);
    }

    @Override
    public void modifyFreelancerProfile(ModifyFreelancerProfileDTO modifyFreelancerProfileDTO, String firebaseUID) {
        FreelancerProfile freelancerProfile = new FreelancerProfile();
        freelancerProfile.setTitle(modifyFreelancerProfileDTO.getTitle());
        freelancerProfile.setDescription(modifyFreelancerProfileDTO.getDescription());
        freelancerProfile.setSkills(modifyFreelancerProfileDTO.getSkills());
        freelancerProfile.setAvailability(modifyFreelancerProfileDTO.getAvailability());
        User user = getUserByFirebaseId(firebaseUID);
        user.setFreelancerProfile(freelancerProfile);
        userRepository.save(user);
    }

    @Override
    public void modifyClientProfile(ModifyClientProfileDTO modifyClientProfileDTO, String firebaseUID) {
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setTitle(modifyClientProfileDTO.getTitle());
        clientProfile.setDescription(modifyClientProfileDTO.getDescription());
        User user = getUserByFirebaseId(firebaseUID);
        user.setClientProfile(clientProfile);
        userRepository.save(user);
    }

    @Override
    public void addOrUpdateDeviceFCMToken(AddDeviceDTO addDeviceDTO, String firebaseUID) {
        User user = getUserByFirebaseId(firebaseUID);
        Map<String, String> devices = user.getDevices();
        if (devices.containsKey(addDeviceDTO.getDeviceModel())) {
            devices.remove(addDeviceDTO.getDeviceModel());
            devices.put(addDeviceDTO.getDeviceModel(), addDeviceDTO.getDeviceToken());
        } else {
            devices.put(addDeviceDTO.getDeviceModel(), addDeviceDTO.getDeviceToken());
        }
        user.setDevices(devices);
        userRepository.save(user);
    }

    @Override
    public boolean existsByFirebaseId(String uid) {
        return userRepository.findByFirebaseId(uid).isPresent();
    }

    @Override
    public User getUserByFirebaseId(String firebaseUID) {
        return userRepository.findByFirebaseId(firebaseUID).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User getUserByUserId(String userID) {
        return userRepository.findById(new ObjectId(userID)).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User getUserByUserId(ObjectId userID) {
        return userRepository.findById(userID).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
