package com.jorgetargz.projectseekerspringboot.controllers.rest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.jorgetargz.projectseekerspringboot.dto.user.ProfileDTO;
import com.jorgetargz.projectseekerspringboot.filters.IsSuperAdmin;
import com.jorgetargz.projectseekerspringboot.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@IsSuperAdmin
@RequestMapping("api/secured/super")
@Tag(name = "Super Admin Endpoints", description = "Project Seeker Administration")
public class SuperAdminController {

    private final UserService userService;
    private final FirebaseAuth firebaseAuth;

    @Autowired
    public SuperAdminController(UserService userService, FirebaseAuth firebaseAuth) {
        this.userService = userService;
        this.firebaseAuth = firebaseAuth;
    }

    @GetMapping("user")
    @Operation(summary = "Get user by email")
    public ProfileDTO getUserByEmail(@RequestParam String email) throws Exception {
        UserRecord userRecord = firebaseAuth.getUserByEmail(email);
        return userService.getProfileByFirebaseId(userRecord.getUid());
    }

    @PutMapping("disable-user")
    @Operation(summary = "Disable user")
    public void disableUser(@RequestParam String email) throws Exception {
        firebaseAuth.updateUser(firebaseAuth.getUserByEmail(email).updateRequest()
                .setDisabled(true));
    }

    @PutMapping("enable-user")
    @Operation(summary = "Enable user")
    public void enableUser(@RequestParam String email) throws Exception {
        firebaseAuth.updateUser(firebaseAuth.getUserByEmail(email).updateRequest()
                .setDisabled(false));
    }

}