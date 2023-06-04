package com.jorgetargz.projectseekerspringboot.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDTO {
    private String id;
    private String firebaseId;
    private String name;
    private String email;
    private String phone;
    private String activeRole;
    private String title;
    private String description;
    private String availability;
    private double rating;
    private List<String> skills;
}
