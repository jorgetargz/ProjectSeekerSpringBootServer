package com.jorgetargz.projectseekerspringboot.dto.user;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String firebaseId;
    private String name;
    private String email;
    private String phone;
    private String activeRole;
}
