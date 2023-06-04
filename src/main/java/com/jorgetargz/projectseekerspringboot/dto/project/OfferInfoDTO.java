package com.jorgetargz.projectseekerspringboot.dto.project;

import lombok.Data;

@Data
public class OfferInfoDTO {
    private String freelancerId;
    private String description;
    private double budget;
    private String status;
}
