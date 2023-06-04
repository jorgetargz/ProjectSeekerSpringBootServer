package com.jorgetargz.projectseekerspringboot.controllers.rest;

import com.jorgetargz.projectseekerspringboot.dto.project.AssignProjectDTO;
import com.jorgetargz.projectseekerspringboot.dto.project.CreateProjectDTO;
import com.jorgetargz.projectseekerspringboot.dto.project.ProjectInfoDTO;
import com.jorgetargz.projectseekerspringboot.dto.project.SubmitOfferDTO;
import com.jorgetargz.projectseekerspringboot.filters.IsClient;
import com.jorgetargz.projectseekerspringboot.filters.IsFreelancer;
import com.jorgetargz.projectseekerspringboot.services.ProjectService;
import com.jorgetargz.projectseekerspringboot.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/secured/projects")
@Tag(name = "Projects Endpoints", description = "Manage projects and offers")
public class ProjectsController {

    private final ProjectService projectService;
    private final SecurityService securityService;

    @Autowired
    public ProjectsController(ProjectService projectService, SecurityService securityService) {
        this.projectService = projectService;
        this.securityService = securityService;
    }

    @GetMapping("/projectInfo")
    @Operation(summary = "Get project info")
    public ProjectInfoDTO getPrjectInfo(@RequestParam("id") String objectId) {
        return projectService.getProjectInfo(objectId);
    }

    @IsClient
    @PostMapping("client/createProject")
    @Operation(summary = "Create project")
    public ProjectInfoDTO createProject(@RequestBody CreateProjectDTO createProjectDTO) {
        String firebaseUID = securityService.getUser().getUid();
        return projectService.createProject(createProjectDTO, firebaseUID);
    }

    @IsFreelancer
    @PostMapping("freelancer/submitOffer")
    @Operation(summary = "Submit offer")
    public ProjectInfoDTO submitOffer(@RequestBody SubmitOfferDTO submitOfferDTO) {
        String firebaseUID = securityService.getUser().getUid();
        projectService.submitOffer(submitOfferDTO, firebaseUID);
        return projectService.getProjectInfo(submitOfferDTO.getProjectId());
    }

    @IsClient
    @PostMapping("client/assignFreelancer")
    @Operation(summary = "Assign freelancer")
    public ProjectInfoDTO assignFreelancer(@RequestBody AssignProjectDTO assignProjectDTO) {
        String clientFirebaseID = securityService.getUser().getUid();
        projectService.assignFreelancer(assignProjectDTO, clientFirebaseID);
        return projectService.getProjectInfo(assignProjectDTO.getProjectId());
    }

    @IsClient
    @PostMapping("client/finishProject")
    @Operation(summary = "Finish project")
    public ProjectInfoDTO finishProject(@RequestParam("id") String projectId) {
        String clientFirebaseID = securityService.getUser().getUid();
        projectService.finishProject(projectId, clientFirebaseID);
        return projectService.getProjectInfo(projectId);
    }

    @GetMapping("/openProjects")
    @Operation(summary = "Get all open projects")
    public List<ProjectInfoDTO> getOpenProjects() {
        return projectService.getOpenProjects();
    }
    
    @PostMapping("/openProjectsBySkills")
    @Operation(summary = "Get open projects by skills")
    public List<ProjectInfoDTO> getOpenProjectsBySkills(@RequestBody List<String> skills) {
        return projectService.getOpenProjectsBySkills(skills);
    }

    @IsFreelancer
    @GetMapping("freelancer/getProjectsWhereIHaveOffer")
    @Operation(summary = "Get projects where I have made an offer")
    public List<ProjectInfoDTO> getProjectsByOfferFreelancer() {
        String freelancerFirebaseId = securityService.getUser().getUid();
        return projectService.getProjectsByOfferFreelancerFirebaseId(freelancerFirebaseId);
    }

    @IsFreelancer
    @GetMapping("freelancer/getProjectsAssignedToMe")
    @Operation(summary = "Get projects assigned to me")
    public List<ProjectInfoDTO> getProjectsByAssignedFreelancer() {
        String freelancerFirebaseId = securityService.getUser().getUid();
        return projectService.getProjectsByAssignedFreelancerFirebaseId(freelancerFirebaseId);
    }

    @IsClient
    @GetMapping("client/myProjects")
    @Operation(summary = "Get the projects I have created")
    public List<ProjectInfoDTO> getProjectsByClient() {
        String clientFirebaseId = securityService.getUser().getUid();
        return projectService.getProjectsByClientFirebaseId(clientFirebaseId);
    }

    @IsClient
    @GetMapping("client/getMyOpenProjects")
    @Operation(summary = "Get the projects I have created that are open")
    public List<ProjectInfoDTO> getOpenProjectsByClient() {
        String clientFirebaseId = securityService.getUser().getUid();
        return projectService.getOpenProjectsByClientFirebaseId(clientFirebaseId);
    }

    @IsClient
    @GetMapping("client/getMyInProgressProjects")
    @Operation(summary = "Get the projects I have created that are in progress")
    public List<ProjectInfoDTO> getInProgressProjectsByClientId() {
        String clientFirebaseId = securityService.getUser().getUid();
        return projectService.getInProgressProjectsByClientFirebaseId(clientFirebaseId);
    }

}