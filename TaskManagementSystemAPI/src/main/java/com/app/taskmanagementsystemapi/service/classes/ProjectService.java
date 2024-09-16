package com.app.taskmanagementsystemapi.service.classes;

import com.app.taskmanagementsystemapi.dto.AssignProjectRequest;
import com.app.taskmanagementsystemapi.dto.ProjectRequest;
import com.app.taskmanagementsystemapi.dto.ProjectResponse;
import com.app.taskmanagementsystemapi.dto.UserResponse;
import com.app.taskmanagementsystemapi.entity.Project;
import com.app.taskmanagementsystemapi.entity.User;
import com.app.taskmanagementsystemapi.repository.ProjectRepository;
import com.app.taskmanagementsystemapi.service.interfaces.IProjectService;
import com.app.taskmanagementsystemapi.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final IUserService userService;

    @Override
    public ResponseEntity<?> createProject(ProjectRequest request) {
        Project projectFromDB = projectRepository.findProjectByTitle(request.getTitle());

        if (projectFromDB != null) {
            return new ResponseEntity<>("Project with title: " + request.getTitle() + " already exists", HttpStatus.NO_CONTENT);
        }

        Project projectToBeSaved = Project.builder().title(request.getTitle()).build();
        projectToBeSaved.setCreatorId(UUID.fromString(userService.getCurrentUser().getBody().getId()));


        projectRepository.save(projectToBeSaved);
        return new ResponseEntity<>("Project created successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> deleteProject(String id) {
        Project projectFromDB = projectRepository.findById(UUID.fromString(id)).orElse(null);
        if (projectFromDB == null) {
            return new ResponseEntity<>("Project doesn't exist", HttpStatus.NO_CONTENT);
        }

        projectRepository.delete(projectFromDB);
        return new ResponseEntity<>("Project deleted succesfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllProjectsOfAuthenticatedAdmin() {
        List<Project> projectsOfAuthenticatedAdmin = projectRepository.findProjectsByCreatorId(UUID.fromString(userService.getCurrentUser().getBody().getId()));

        List<ProjectResponse> projects = projectsOfAuthenticatedAdmin
                .stream()
                .map(this::mapToProjectResponse)
                .toList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> assignProject(AssignProjectRequest request) {
        Project project = projectRepository.findById(UUID.fromString(request.getProjectId())).orElse(null);
        User projectManager = userService.getManagerById(request.getProjectManagerId());

        if (projectManager == null || project == null) {
            return new ResponseEntity<>("Manager or project not found", HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (projectManager.getProjectToBeManaged() != null) {
            return new ResponseEntity<>("Manager is busy at this time", HttpStatus.CONFLICT);
        }

        project.setProjectManager(projectManager);
//        List<Project> projectsList = new ArrayList<>(projectManager.getProjects());
//        projectsList.add(project);
//        projectManager.setProjects(projectsList);

        projectRepository.save(project);
        return new ResponseEntity<>("Project " + project.getTitle()
                + " is successfully assigned to "
                + projectManager.getFirtsname() + " " + projectManager.getLastname()
                , HttpStatus.OK);
    }

    @Override
    public Project getProjectById(String id) {
        return projectRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public Project getProjectByProjectManager(User projectManager) {
        return projectRepository.findProjectByProjectManager(projectManager);
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        if (project == null) {
            return null;
        } else {
            return ProjectResponse.builder()
                    .id(project.getId().toString())
                    .projectManagerFirstname(project.getProjectManager() == null ? null : project.getProjectManager().getFirtsname())
                    .projectManagerLastname(project.getProjectManager() == null ? null : project.getProjectManager().getLastname())
                    .title(project.getTitle())
                    .createdAt(project.getCreatedAt())
                    .creatorId(project.getCreatorId().toString())
                    .build();
        }
    }


}
