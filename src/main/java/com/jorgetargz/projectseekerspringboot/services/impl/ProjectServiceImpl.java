package com.jorgetargz.projectseekerspringboot.services.impl;

import com.jorgetargz.projectseekerspringboot.config.properties.AppRolesProperties;
import com.jorgetargz.projectseekerspringboot.data.ProjectRepository;
import com.jorgetargz.projectseekerspringboot.data.collections.project.Offer;
import com.jorgetargz.projectseekerspringboot.data.collections.project.Project;
import com.jorgetargz.projectseekerspringboot.data.collections.user.User;
import com.jorgetargz.projectseekerspringboot.dto.project.*;
import com.jorgetargz.projectseekerspringboot.exceptions.BadRequestException;
import com.jorgetargz.projectseekerspringboot.exceptions.NotFoundException;
import com.jorgetargz.projectseekerspringboot.exceptions.UnauthorizedException;
import com.jorgetargz.projectseekerspringboot.services.NotificationsService;
import com.jorgetargz.projectseekerspringboot.services.ProjectService;
import com.jorgetargz.projectseekerspringboot.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final NotificationsService notificationService;
    private final AppRolesProperties appRolesProperties;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserService userService, NotificationsService notificationService, AppRolesProperties appRolesProperties) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.appRolesProperties = appRolesProperties;
    }

    @Override
    public ProjectInfoDTO getProjectInfo(String objectId) {
        ObjectId projectId = new ObjectId(objectId);
        return mapFromProjectDocument(getProjectById(projectId));
    }

    @Override
    public ProjectInfoDTO createProject(CreateProjectDTO createProjectDTO, String clientFirebaseId) {
        User user = userService.getUserByFirebaseId(clientFirebaseId);
        validateIsClient(user);
        validateCreateProjectDTO(createProjectDTO);
        Project project = new Project();
        project.setClientId(user.get_id());
        project.setTitle(createProjectDTO.getTitle());
        project.setDescription(createProjectDTO.getDescription());
        project.setSkills(createProjectDTO.getSkills());
        project.setMinBudget(createProjectDTO.getMinBudget());
        project.setMaxBudget(createProjectDTO.getMaxBudget());
        project.setDeadlineDate(createProjectDTO.getDeadlineDate());
        return mapFromProjectDocument(projectRepository.save(project));
    }

    private void validateCreateProjectDTO(CreateProjectDTO createProjectDTO) {
        String message = "";
        if (createProjectDTO.getTitle().isEmpty()) {
            message += "Title is required\n";
        }
        if (createProjectDTO.getDescription().isEmpty()) {
            message += "Description is required\n";
        }
        if (createProjectDTO.getSkills().isEmpty()) {
            message += "Skills are required\n";
        }
        if (createProjectDTO.getMinBudget() > createProjectDTO.getMaxBudget()) {
            message += "Min budget must be less than max budget\n";
        }
        if (createProjectDTO.getStartDate().isBefore(LocalDate.now())) {
            message += "Deadline date must be after today\n";
        }
        if (createProjectDTO.getDeadlineDate().isBefore(createProjectDTO.getStartDate())) {
            message += "Deadline date must be after start date\n";
        }
        if (!message.isEmpty()) {
            throw new BadRequestException(message);
        }
    }

    @Override
    public void submitOffer(SubmitOfferDTO submitOfferDTO, String freelancerFirebaseId) {
        User user = userService.getUserByFirebaseId(freelancerFirebaseId);
        validateIsFreelancer(user);
        Offer offer = new Offer();
        offer.setFreelancerId(user.get_id());
        offer.setDescription(submitOfferDTO.getDescription());
        offer.setBudget(submitOfferDTO.getBudget());
        ObjectId projectId = new ObjectId(submitOfferDTO.getProjectId());
        Project project = getProjectById(projectId);
        project.getOffers().removeIf(offer1 -> offer1.getFreelancerId().equals(user.get_id()));
        project.getOffers().add(offer);
        User client = userService.getUserByUserId(project.getClientId());
        notificationService.sendNotification(client.getDevices(), "New offer", "You have a new offer for your project " + project.getTitle());
        projectRepository.save(project);
    }

    @Override
    public void assignFreelancer(AssignProjectDTO assignProjectDTO, String clientFirebaseID) {
        ObjectId projectId = new ObjectId(assignProjectDTO.getProjectId());
        ObjectId freelancerId = new ObjectId(assignProjectDTO.getFreelancerId());
        Project project = getProjectById(projectId);
        User client = userService.getUserByFirebaseId(clientFirebaseID);
        validateIsPossibleToAssignProject(freelancerId, project, client);
        project.getOffers().forEach(offer -> {
            if (offer.getFreelancerId().equals(freelancerId)) {
                offer.setStatus("ACCEPTED");
            } else {
                offer.setStatus("REJECTED");
            }
        });
        project.setSelectedFreelancerId(freelancerId);
        project.setStatus("IN_PROGRESS");
        User freelancer = userService.getUserByUserId(freelancerId);
        notificationService.sendNotification(freelancer.getDevices(), "Project assigned", "You have been assigned to the project " + project.getTitle());
        projectRepository.save(project);
    }

    private void validateIsPossibleToAssignProject(ObjectId freelancerId, Project project, User client) {
        validateIsOwner(project, client);
        String message = "";
        if (project.getOffers().stream().noneMatch(offer -> offer.getFreelancerId().equals(freelancerId))) {
            message += "Offer not found\n";
        }
        if (project.getSelectedFreelancerId() != null) {
            message += "This project already has a freelancer assigned\n";
        }
        if (!message.isEmpty()) {
            throw new BadRequestException(message);
        }
    }

    private void validateIsOwner(Project project, User client) {
        if (!project.getClientId().toHexString().equals(client.get_id().toHexString())) {
            throw new UnauthorizedException("You are not the owner of this project");
        }
    }

    @Override
    public void finishProject(String projectId, String clientFirebaseID) {
        ObjectId objectId = new ObjectId(projectId);
        Project project = getProjectById(objectId);
        User client = userService.getUserByFirebaseId(clientFirebaseID);
        validateProjectCanBeFinished(project, client);
        project.setStatus("FINISHED");
        User freelancer = userService.getUserByUserId(project.getSelectedFreelancerId());
        notificationService.sendNotification(freelancer.getDevices(), "Project finished", "The project " + project.getTitle() + " has been finished");
        projectRepository.save(project);
    }

    private void validateProjectCanBeFinished(Project project, User client) {
        validateIsOwner(project, client);
        if (!project.getStatus().equals("IN_PROGRESS")) {
            throw new BadRequestException("This project is not in progress");
        }
    }

    @Override
    public List<ProjectInfoDTO> getProjectsByOfferFreelancerFirebaseId(String freelancerFirebaseId) {
        User user = userService.getUserByFirebaseId(freelancerFirebaseId);
        validateIsFreelancer(user);
        return projectRepository.findByOffersFreelancerId(user.get_id()).stream().map(
                this::mapFromProjectDocument
        ).toList();
    }

    private void validateIsFreelancer(User user) {
        if (!user.getActiveRole().equalsIgnoreCase(appRolesProperties.getFreelancer())) {
            throw new UnauthorizedException("Change your role to freelancer");
        }
    }

    @Override
    public List<ProjectInfoDTO> getProjectsByAssignedFreelancerFirebaseId(String freelancerFirebaseId) {
        User user = userService.getUserByFirebaseId(freelancerFirebaseId);
        validateIsFreelancer(user);
        return projectRepository.findBySelectedFreelancerId(user.get_id()).stream().map(
                this::mapFromProjectDocument
        ).toList();
    }

    @Override
    public List<ProjectInfoDTO> getProjectsByClientFirebaseId(String clientFirebaseID) {
        User user = userService.getUserByFirebaseId(clientFirebaseID);
        validateIsClient(user);
        return projectRepository.findByClientId(user.get_id()).stream().map(
                this::mapFromProjectDocument
        ).toList();
    }

    private void validateIsClient(User user) {
        if (!user.getActiveRole().equalsIgnoreCase(appRolesProperties.getClient())) {
            throw new UnauthorizedException("Change your role to client");
        }
    }

    @Override
    public List<ProjectInfoDTO> getOpenProjectsByClientFirebaseId(String clientFirebaseID) {
        User user = userService.getUserByFirebaseId(clientFirebaseID);
        validateIsClient(user);
        return projectRepository.findByClientIdAndStatus(user.get_id(), "OPEN").stream().map(
                this::mapFromProjectDocument
        ).toList();
    }

    @Override
    public List<ProjectInfoDTO> getInProgressProjectsByClientFirebaseId(String clientFirebaseID) {
        User user = userService.getUserByFirebaseId(clientFirebaseID);
        validateIsClient(user);
        return projectRepository.findByClientIdAndStatus(user.get_id(), "IN_PROGRESS").stream().map(
                this::mapFromProjectDocument
        ).toList();
    }

    @Override
    public List<ProjectInfoDTO> getOpenProjects() {
        return projectRepository.findByStatus("OPEN").stream().map(
                this::mapFromProjectDocument
        ).toList();
    }

    @Override
    public List<ProjectInfoDTO> getOpenProjectsBySkills(List<String> skills) {
        List<Project> projects = new ArrayList<>();
        skills.forEach(skill -> projects.addAll(projectRepository.findBySkillsContainsAndStatus(skill, "OPEN")));
        return projects.stream().distinct().map(this::mapFromProjectDocument).toList();
    }

    private Project getProjectById(ObjectId projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private ProjectInfoDTO mapFromProjectDocument(Project project) {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setId(project.get_id().toHexString());
        projectInfoDTO.setClientId(project.getClientId().toHexString());
        projectInfoDTO.setTitle(project.getTitle());
        projectInfoDTO.setDescription(project.getDescription());
        projectInfoDTO.setStartDate(project.getStartDate());
        projectInfoDTO.setDeadlineDate(project.getDeadlineDate());
        projectInfoDTO.setMaxBudget(project.getMaxBudget());
        projectInfoDTO.setMinBudget(project.getMinBudget());
        projectInfoDTO.setSkills(project.getSkills());
        projectInfoDTO.setRealEndDate(project.getRealEndDate());
        projectInfoDTO.setStatus(project.getStatus());
        projectInfoDTO.setOffers(project.getOffers().stream().map(
                this::mapFronOfferDocument
        ).toList());
        if (project.getSelectedFreelancerId() != null) {
            projectInfoDTO.setSelectedFreelancerId(project.getSelectedFreelancerId().toHexString());
        }
        return projectInfoDTO;
    }

    private OfferInfoDTO mapFronOfferDocument(Offer offer) {
        OfferInfoDTO offerInfoDTO = new OfferInfoDTO();
        offerInfoDTO.setFreelancerId(offer.getFreelancerId().toHexString());
        offerInfoDTO.setDescription(offer.getDescription());
        offerInfoDTO.setBudget(offer.getBudget());
        offerInfoDTO.setStatus(offer.getStatus());
        return offerInfoDTO;
    }
}