package com.jorgetargz.projectseekerspringboot.data.collections.project;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Offer {
    private ObjectId freelancerId;
    private String description;
    private double budget;
    private String status;

    public Offer() {
        freelancerId = null;
        budget = 0;
        status = "PENDING";
    }
}
