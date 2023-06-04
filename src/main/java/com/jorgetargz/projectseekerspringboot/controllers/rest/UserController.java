package com.jorgetargz.projectseekerspringboot.controllers.rest;


import com.jorgetargz.projectseekerspringboot.dto.user.*;
import com.jorgetargz.projectseekerspringboot.filters.IsClient;
import com.jorgetargz.projectseekerspringboot.filters.IsFreelancer;
import com.jorgetargz.projectseekerspringboot.filters.IsSuperAdmin;
import com.jorgetargz.projectseekerspringboot.services.SecurityService;
import com.jorgetargz.projectseekerspringboot.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secured/users")
@Tag(name = "Users Endpoints", description = "Manage users")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/myProfile")
    @Operation(summary = "Get my profile")
    public ProfileDTO getMyProfile() {
        String firebaseUID = securityService.getUser().getUid();
        return userService.getProfileByFirebaseId(firebaseUID);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get profile")
    public ProfileDTO getProfile(@RequestParam("id") String userID) {
        return userService.getProfileByUserId(userID);
    }

    @IsSuperAdmin
    @PostMapping("/createUser")
    @Operation(summary = "Create user")
    public ProfileDTO createUser(@RequestBody CreateUserDTO createUserDTO) {
        userService.createUser(createUserDTO);
        String firebaseUID = securityService.getUser().getUid();
        return userService.getProfileByFirebaseId(firebaseUID);
    }

    @PostMapping("/addDevice")
    @Operation(summary = "Add device for notifications")
    public void addOrUpdateDeviceFCMToken(@RequestBody AddDeviceDTO addDeviceDTO) {
        String firebaseUID = securityService.getUser().getUid();
        userService.addOrUpdateDeviceFCMToken(addDeviceDTO, firebaseUID);
    }

    @DeleteMapping("/deleteMyAccount")
    @Operation(summary = "Delete my account")
    public void deleteUser() {
        String firebaseUID = securityService.getUser().getUid();
        userService.deleteUser(firebaseUID);
    }

    @PutMapping("/changeRole")
    @Operation(summary = "Change user role")
    public ProfileDTO changeRole(@RequestBody ChangeUserRoleDTO changeUserRoleDTO) {
        String firebaseUID = securityService.getUser().getUid();
        userService.changeRole(changeUserRoleDTO, firebaseUID);
        return userService.getProfileByFirebaseId(firebaseUID);
    }

    @IsFreelancer
    @PutMapping("/modifyFreelancerProfile")
    @Operation(summary = "Modify freelancer profile")
    public ProfileDTO modifyFreelancerProfile(@RequestBody ModifyFreelancerProfileDTO modifyFreelancerProfileDTO) {
        String firebaseUID = securityService.getUser().getUid();
        userService.modifyFreelancerProfile(modifyFreelancerProfileDTO, firebaseUID);
        return userService.getProfileByFirebaseId(firebaseUID);
    }

    @IsClient
    @PutMapping("/modifyClientProfile")
    @Operation(summary = "Modify client profile")
    public ProfileDTO modifyClientProfile(@RequestBody ModifyClientProfileDTO modifyFreelancerProfileDTO) {
        String firebaseUID = securityService.getUser().getUid();
        userService.modifyClientProfile(modifyFreelancerProfileDTO, firebaseUID);
        return userService.getProfileByFirebaseId(firebaseUID);
    }

}