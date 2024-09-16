package com.app.taskmanagementsystemapi.controller;

import com.app.taskmanagementsystemapi.dto.AssignProjectRequest;
import com.app.taskmanagementsystemapi.dto.ProjectRequest;
import com.app.taskmanagementsystemapi.service.interfaces.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    @PostMapping("/admin/create-project")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest request){
        return projectService.createProject(request);
    }

    @DeleteMapping("/admin/delete-project")
    public ResponseEntity<?> deleteProject(@RequestParam String id){
        return projectService.deleteProject(id);
    }

    @GetMapping("/admin/all-projects")
    public ResponseEntity<?> getAllProjects(){
        return projectService.getAllProjectsOfAuthenticatedAdmin();
    }

    @PutMapping("/admin/assign-project")
    public ResponseEntity<?> assignProjectToProjectManager(@RequestBody AssignProjectRequest request){
        return projectService.assignProject(request);
    }
}
