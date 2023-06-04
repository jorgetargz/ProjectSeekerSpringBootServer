package com.jorgetargz.projectseekerspringboot.data.collections.user;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FreelancerProfile {
    private String title;
    private String description;
    private List<String> skills;
    private String availability;

    public FreelancerProfile() {
        title = "";
        description = "";
        skills = new ArrayList<>();
        availability = "";
    }
}
