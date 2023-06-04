package com.jorgetargz.projectseekerspringboot.data.collections.user;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "Users")
public class User {

    @Id
    private ObjectId _id;
    @Indexed(unique = true)
    private String firebaseId;
    private String name;
    private String email;
    private String phone;
    private String activeRole;
    private FreelancerProfile freelancerProfile;
    private ClientProfile clientProfile;
    private Map<String, String> devices;

    public User() {
        _id = new ObjectId();
        firebaseId = "";
        name = "";
        email = "";
        phone = "";
        activeRole = "ROLE_FREELANCER";
        freelancerProfile = new FreelancerProfile();
        clientProfile = new ClientProfile();
        devices = new HashMap<>();
    }
}