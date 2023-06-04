package com.jorgetargz.projectseekerspringboot.data;

import com.jorgetargz.projectseekerspringboot.data.collections.project.Project;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, ObjectId> {
    List<Project> findByClientId(ObjectId clientId);
    List<Project> findByClientIdAndStatus(ObjectId clientId, String status);
    List<Project> findByStatus(String status);
    List<Project> findBySelectedFreelancerId(ObjectId freelancerId);
    List<Project> findByOffersFreelancerId(ObjectId freelancerId);
    List<Project> findBySkillsContainsAndStatus(String skill, String status);
}
