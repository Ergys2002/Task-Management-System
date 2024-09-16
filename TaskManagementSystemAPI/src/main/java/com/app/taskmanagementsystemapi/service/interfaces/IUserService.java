package com.app.taskmanagementsystemapi.service.interfaces;

import com.app.taskmanagementsystemapi.dto.*;
import com.app.taskmanagementsystemapi.entity.User;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<LoginResponse> register(RegisterRequest request);

    ResponseEntity<LoginResponse> login(LoginRequest request);

    ResponseEntity<UserResponse> getCurrentUser();

    ResponseEntity<?> addProjectManager(AddUserRequest request);

    ResponseEntity<?> deleteProjectManager(String id);

    ResponseEntity<?> getAllProjectManagersCreatedByAuthenticatedAdmin();

    User getManagerById(String projectManagerId);

    ResponseEntity<?> addDeveloper(AddUserRequest request);

    ResponseEntity<?> deleteDeveloper(String id);

    User getDeveloperById(String id);

    ResponseEntity<?> getAllDevelopersCreatedByAuthenticatedManager();
}
