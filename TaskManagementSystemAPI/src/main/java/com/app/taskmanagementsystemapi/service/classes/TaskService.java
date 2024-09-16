package com.app.taskmanagementsystemapi.service.classes;

import com.app.taskmanagementsystemapi.dto.AddTaskRequest;
import com.app.taskmanagementsystemapi.dto.AssignTaskRequest;
import com.app.taskmanagementsystemapi.dto.ProjectResponse;
import com.app.taskmanagementsystemapi.dto.TaskResponse;
import com.app.taskmanagementsystemapi.entity.Project;
import com.app.taskmanagementsystemapi.entity.Task;
import com.app.taskmanagementsystemapi.entity.User;
import com.app.taskmanagementsystemapi.enums.TaskStatus;
import com.app.taskmanagementsystemapi.repository.TaskRepository;
import com.app.taskmanagementsystemapi.service.interfaces.IProjectService;
import com.app.taskmanagementsystemapi.service.interfaces.ITaskService;
import com.app.taskmanagementsystemapi.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final IUserService userService;
    private final IProjectService projectService;
    @Override
    public ResponseEntity<?> createTask(AddTaskRequest request) {
        Task taskToBeSaved = Task.builder()
                .taskStatus(TaskStatus.READY)
                .title(request.getTitle())
                .deadline(calculateDeadline(request.getDeadlineDate(), request.getDeadlineTime()))
                .description(request.getDescription())
                .build();

        String currentUserId = userService.getCurrentUser().getBody().getId();
        User currentUser = userService.getManagerById(currentUserId);

        Project projectOfTask = projectService.getProjectByProjectManager(currentUser);
        System.out.println(projectOfTask);

        taskToBeSaved.setCreatorId(UUID.fromString(currentUserId));
        taskToBeSaved.setProject(projectOfTask);

        taskRepository.save(taskToBeSaved);
        return new ResponseEntity<>("Task saved succesfully", HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<?> deleteTask(String id) {
        Task taskToBeDeleted = taskRepository.findById(UUID.fromString(id)).orElse(null);

        if (taskToBeDeleted == null){
            return new ResponseEntity<>("Task doesn't exist" , HttpStatus.NO_CONTENT);
        }

        taskRepository.delete(taskToBeDeleted);
        return new ResponseEntity<>("Task deleted succesfully" , HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> assignTask(AssignTaskRequest request) {
        Task task = taskRepository.findById(UUID.fromString(request.getTaskId())).orElse(null);

        if(task == null) {
            return new ResponseEntity<>("Task doesn't exist", HttpStatus.NO_CONTENT);
        }else {
            User developer = userService.getDeveloperById(request.getDeveloperId());
            if (developer == null){
                return new ResponseEntity<>("Developer doesn't exist", HttpStatus.NO_CONTENT);
            }

            task.setUser(developer);
            task.setTaskStatus(TaskStatus.ASSIGNED);
            taskRepository.save(task);

            return new ResponseEntity<>("Task assigned successfully" , HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> getAllTasksOfAuthenticatedProjectManager() {
        UUID currentManagerId = UUID.fromString(userService.getCurrentUser().getBody().getId());
        List<Task> taskList = taskRepository.findAllByCreatorId(currentManagerId);

        return new ResponseEntity<>(
                taskList
                        .stream()
                        .map(this::mapToTaskResponse)
                        .collect(Collectors.toList())
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getTaskById(String id) {
        Task task = taskRepository.findById(UUID.fromString(id)).orElse(null);

        if(task == null){
            return new ResponseEntity<>("Task doesn't exist", HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(mapToTaskResponse(task), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllTasksOfAuthenticatedDeveloper() {
        String currentUserId = userService.getCurrentUser().getBody().getId();
        User currentUser = userService.getDeveloperById(currentUserId);

        List<Task> tasks = taskRepository.findTasksByUser(currentUser).stream().filter(task -> task.getTaskStatus() != TaskStatus.FINISHED).toList();
        if (tasks.isEmpty()){
            return new ResponseEntity<>("No tasks found for this developer" , HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(tasks.stream().map(this::mapToTaskResponse).collect(Collectors.toList()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeTaskStatus(String id, String taskStatus) {
        Task task = taskRepository.findById(UUID.fromString(id)).orElse(null);

        if (task == null) {
            return new ResponseEntity<>("Task doesn't exist", HttpStatus.NO_CONTENT);
        }

        if (TaskStatus.valueOf(taskStatus).equals(TaskStatus.IN_PROGRESS)){
            if (task.getTaskStatus().equals(TaskStatus.ASSIGNED) || task.getTaskStatus().equals(TaskStatus.PAUSED)){
                task.setTaskStatus(TaskStatus.valueOf(taskStatus));
                taskRepository.save(task);
                return new ResponseEntity<>("Task status changed successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Task status  can not be changed !", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else if(TaskStatus.valueOf(taskStatus).equals(TaskStatus.TO_BE_REVIEWED) || TaskStatus.valueOf(taskStatus).equals(TaskStatus.PAUSED)){
            if (task.getTaskStatus().equals(TaskStatus.IN_PROGRESS))
                task.setTaskStatus(TaskStatus.valueOf(taskStatus));
            taskRepository.save(task);
            return new ResponseEntity<>("Task status changed successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Task status  can not be changed !", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> reviewTask(String id, String taskStatus) {
        Task task = taskRepository.findById(UUID.fromString(id)).orElse(null);

        if (task == null) {
            return new ResponseEntity<>("Task doesn't exist", HttpStatus.NO_CONTENT);
        }

        if(TaskStatus.valueOf(taskStatus).equals(TaskStatus.FINISHED) || TaskStatus.valueOf(taskStatus).equals(TaskStatus.IN_PROGRESS)){
            if (task.getTaskStatus().equals(TaskStatus.TO_BE_REVIEWED))
                task.setTaskStatus(TaskStatus.valueOf(taskStatus));
            taskRepository.save(task);
            return new ResponseEntity<>("Task status changed successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Task status can not be changed !", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<?> getToBeReviewed() {
        User currentUser = userService.getManagerById(userService.getCurrentUser().getBody().getId());
        List<Task> tasksToBeReviewed = taskRepository
                .findAll()
                .stream()
                .filter(task -> task.getTaskStatus().equals(TaskStatus.TO_BE_REVIEWED) && task.getCreatorId().equals(currentUser.getId())).toList();
        if (tasksToBeReviewed.isEmpty()){
            return new ResponseEntity<>("No tasks to be reviewed", HttpStatus.OK);
        }

        return new ResponseEntity<>(tasksToBeReviewed.stream().map(this::mapToTaskResponse).collect(Collectors.toList()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getFinishedTasks() {
        User currentUser = userService.getManagerById(userService.getCurrentUser().getBody().getId());
        List<Task> tasksToBeReviewed = taskRepository
                .findTasksByUser(currentUser)
                .stream()
                .filter(task -> task.getTaskStatus().equals(TaskStatus.FINISHED)).toList();

        if (tasksToBeReviewed.isEmpty()){
            return new ResponseEntity<>("No finished tasks", HttpStatus.OK);
        }

        return new ResponseEntity<>(tasksToBeReviewed.stream().map(this::mapToTaskResponse).collect(Collectors.toList()), HttpStatus.OK);
    }


    private LocalDateTime calculateDeadline(LocalDate deadlineDate, LocalTime deadlineTime) {
        return LocalDateTime.of(
                deadlineDate.getYear(),
                deadlineDate.getMonth(),
                deadlineDate.getDayOfMonth(),
                deadlineTime.getHour(),
                deadlineTime.getMinute(),
                0
        );
    }

    private TaskResponse mapToTaskResponse(Task task){
        if(task == null){
            return null;
        }

        return TaskResponse.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .taskStatus(task.getTaskStatus())
                .createdAt(task.getCreatedAt())
                .deadline(task.getDeadline())
                .projectManagerLastname(task.getProject().getProjectManager().getLastname())
                .projectManagerFirstname(task.getProject().getProjectManager().getFirtsname())
                .projectId(task.getProject().getId().toString())
                .developerId(task.getUser()==null ? null : task.getUser().getId().toString())
                .projectTitle(task.getProject().getTitle())
                .description(task.getDescription())
                .developerFirstname(task.getUser() == null ? null : task.getUser().getFirtsname())
                .developerLastname(task.getUser() == null ? null : task.getUser().getLastname())
                .build();
    }



}
