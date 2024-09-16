package com.app.taskmanagementsystemapi.controller;

import com.app.taskmanagementsystemapi.dto.AddTaskRequest;
import com.app.taskmanagementsystemapi.dto.AssignTaskRequest;
import com.app.taskmanagementsystemapi.service.interfaces.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

    @PostMapping("/manager/create-task")
    public ResponseEntity<?> createTask(@RequestBody AddTaskRequest request){
        return taskService.createTask(request);
    }
    @DeleteMapping("/manager/delete-task")
    public ResponseEntity<?> deleteTask(@RequestParam("id") String id){
        return taskService.deleteTask(id);
    }

    @PutMapping("/manager/assign-task")
    public ResponseEntity<?> assignTaskToDeveloper(@RequestBody AssignTaskRequest request){
        return taskService.assignTask(request);
    }

    @GetMapping("/manager/all-tasks")
    public ResponseEntity<?> getAllTasksOfAuthenticatedProjectManager(){
        return taskService.getAllTasksOfAuthenticatedProjectManager();
    }

    @GetMapping("/manager/task-by-id")
    public ResponseEntity<?> getTaskById(@RequestParam("id") String id){
        return taskService.getTaskById(id);
    }

    @GetMapping("/developer/tasks-of-developer")
    public ResponseEntity<?> getTasksOfAuthenticatedDeveloper(){
        return taskService.getAllTasksOfAuthenticatedDeveloper();
    }
    @PutMapping("/developer/change-status")
    public ResponseEntity<?> changeTaskStatus(@RequestParam("taskId") String id,@RequestParam("status") String taskStatus){
        return taskService.changeTaskStatus(id, taskStatus);
    }

    @PutMapping("/manager/review-task")
    public ResponseEntity<?> reviewTask(@RequestParam("taskId") String id,@RequestParam("status") String taskStatus){
        return taskService.reviewTask(id, taskStatus);
    }

    @GetMapping("/manager/to-be-reviewed")
    public ResponseEntity<?> getToBeReviewedTasks(){
        return taskService.getToBeReviewed();
    }

    @GetMapping("/developer/finished-tasks")
    public ResponseEntity<?> finshedTasks(){
        return taskService.getFinishedTasks();
    }
}

