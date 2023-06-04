package com.jorgetargz.projectseekerspringboot.services;

import com.jorgetargz.projectseekerspringboot.dto.project.AssignProjectDTO;
import com.jorgetargz.projectseekerspringboot.dto.project.CreateProjectDTO;
import com.jorgetargz.projectseekerspringboot.dto.project.ProjectInfoDTO;
import com.jorgetargz.projectseekerspringboot.dto.project.SubmitOfferDTO;

import java.util.List;

public interface ProjectService {
    ProjectInfoDTO getProjectInfo(String objectId);

    ProjectInfoDTO createProject(CreateProjectDTO createProjectDTO, String clientFirebaseId);

    void submitOffer(SubmitOfferDTO submitOfferDTO, String freelancerFirebaseId);

    void assignFreelancer(AssignProjectDTO assignProjectDTO, String clientFirebaseID);

    void finishProject(String projectId, String clientFirebaseID);

    List<ProjectInfoDTO> getProjectsByOfferFreelancerFirebaseId(String freelancerFirebaseId);

    List<ProjectInfoDTO> getProjectsByAssignedFreelancerFirebaseId(String freelancerFirebaseId);

    List<ProjectInfoDTO> getProjectsByClientFirebaseId(String clientFirebaseID);

    List<ProjectInfoDTO> getOpenProjectsByClientFirebaseId(String clientFirebaseID);

    List<ProjectInfoDTO> getInProgressProjectsByClientFirebaseId(String clientFirebaseID);

    List<ProjectInfoDTO> getOpenProjects();

    List<ProjectInfoDTO> getOpenProjectsBySkills(List<String> skills);
}
