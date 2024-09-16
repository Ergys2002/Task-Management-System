package com.app.taskmanagementsystemapi.controller;

import com.app.taskmanagementsystemapi.dto.*;
import com.app.taskmanagementsystemapi.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return userService.login(request);
    }

    @PostMapping("/admin/add-project-manager")
    public ResponseEntity<?> addProjectManager(@RequestBody AddUserRequest request){
        return userService.addProjectManager(request);
    }

    @DeleteMapping("/admin/delete-project-manager")
    public ResponseEntity<?> deleteProjectManager(@RequestParam(name = "id") String id){
        return userService.deleteProjectManager(id);
    }

    @GetMapping("/admin/all-project-managers")
    public ResponseEntity<?> getAllProjectManagers(){
        return userService.getAllProjectManagersCreatedByAuthenticatedAdmin();
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request){
        return userService.register(request);
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> getCurrentUser(){
        return userService.getCurrentUser();
    }

    @PostMapping("/manager/add-developer")
    public ResponseEntity<?> addDeveloper(@RequestBody AddUserRequest request){
        return userService.addDeveloper(request);
    }

    @DeleteMapping("/manager/delete-developer")
    public ResponseEntity<?> deleteDeveloper(@RequestParam(name = "id") String id){
        return userService.deleteDeveloper(id);
    }

    @GetMapping("/manager/all-developers")
    public ResponseEntity<?> getAllDevelopers(){
        return userService.getAllDevelopersCreatedByAuthenticatedManager();
    }



}
