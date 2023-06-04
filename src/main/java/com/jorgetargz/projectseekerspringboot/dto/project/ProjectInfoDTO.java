package com.jorgetargz.projectseekerspringboot.dto.project;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectInfoDTO {
    private String id;
    private String clientId;
    private String title;
    private String description;
    private List<String> skills;
    private double minBudget;
    private double maxBudget;
    private LocalDate startDate;
    private LocalDate deadlineDate;
    private LocalDate realEndDate;
    private String status;
    private String selectedFreelancerId;
    private List<OfferInfoDTO> offers;
}
