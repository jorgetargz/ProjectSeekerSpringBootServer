package com.jorgetargz.projectseekerspringboot.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class ModifyFreelancerProfileDTO {
    private String title;
    private String description;
    private List<String> skills;
    private String availability;
}
