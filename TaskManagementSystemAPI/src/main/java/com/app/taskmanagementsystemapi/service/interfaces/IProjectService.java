package com.app.taskmanagementsystemapi.service.interfaces;

import com.app.taskmanagementsystemapi.dto.AssignProjectRequest;
import com.app.taskmanagementsystemapi.dto.ProjectRequest;
import com.app.taskmanagementsystemapi.dto.ProjectResponse;
import com.app.taskmanagementsystemapi.entity.Project;
import com.app.taskmanagementsystemapi.entity.User;
import org.springframework.http.ResponseEntity;

public interface IProjectService {
    ResponseEntity<?> createProject(ProjectRequest request);
    ResponseEntity<?> deleteProject(String id);
    ResponseEntity<?> getAllProjectsOfAuthenticatedAdmin();
    ResponseEntity<?> assignProject(AssignProjectRequest request);
    Project getProjectById(String id);
    Project getProjectByProjectManager(User id);
}
