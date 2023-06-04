package com.jorgetargz.projectseekerspringboot.data.collections.project;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "Projects")
public class Project {

    @Id
    private ObjectId _id;
    private ObjectId clientId;
    private String title;
    private String description;
    private List<String> skills;
    private double minBudget;
    private double maxBudget;
    private LocalDate startDate;
    private LocalDate deadlineDate;
    private LocalDate realEndDate;
    private String status;
    private ObjectId selectedFreelancerId;
    private List<Offer> offers;

    public Project() {
        _id = new ObjectId();
        clientId = null;
        title = "Undefined Title";
        description = "Undefined description";
        skills = new ArrayList<>();
        minBudget = 0;
        maxBudget = 0;
        startDate = LocalDate.now();
        deadlineDate = null;
        realEndDate = null;
        status = "OPEN";
        selectedFreelancerId = null;
        offers = new ArrayList<>();
    }
}
