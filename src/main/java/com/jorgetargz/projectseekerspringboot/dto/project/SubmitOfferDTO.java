package com.jorgetargz.projectseekerspringboot.dto.project;

import lombok.Data;

@Data
public class SubmitOfferDTO {
    private String projectId;
    private String description;
    private double budget;
}
