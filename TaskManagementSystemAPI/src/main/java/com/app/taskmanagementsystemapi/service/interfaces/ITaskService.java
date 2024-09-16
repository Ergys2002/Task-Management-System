package com.app.taskmanagementsystemapi.service.interfaces;

import com.app.taskmanagementsystemapi.dto.AddTaskRequest;
import com.app.taskmanagementsystemapi.dto.AssignTaskRequest;
import org.springframework.http.ResponseEntity;

public interface ITaskService {
    ResponseEntity<?> createTask(AddTaskRequest request);

    ResponseEntity<?> deleteTask(String id);

    ResponseEntity<?> assignTask(AssignTaskRequest request);

    ResponseEntity<?> getAllTasksOfAuthenticatedProjectManager();

    ResponseEntity<?> getTaskById(String id);

    ResponseEntity<?> getAllTasksOfAuthenticatedDeveloper();

    ResponseEntity<?> changeTaskStatus(String id, String taskStatus);

    ResponseEntity<?> reviewTask(String id, String taskStatus);

    ResponseEntity<?> getToBeReviewed();

    ResponseEntity<?> getFinishedTasks();
}
