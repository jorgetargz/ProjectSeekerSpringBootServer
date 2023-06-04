package com.jorgetargz.projectseekerspringboot.dto.project;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateProjectDTO {
    private String title;
    private String description;
    private List<String> skills;
    private double minBudget;
    private double maxBudget;
    private LocalDate startDate;
    private LocalDate deadlineDate;
}
