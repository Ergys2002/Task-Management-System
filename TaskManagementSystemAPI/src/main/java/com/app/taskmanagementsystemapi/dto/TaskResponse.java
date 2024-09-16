package com.app.taskmanagementsystemapi.dto;

import com.app.taskmanagementsystemapi.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private String id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private String projectManagerFirstname;
    private String projectManagerLastname;
    private TaskStatus taskStatus;
    private String projectTitle;
    private String projectId;
    private String developerId;
    private String description;
    private String developerFirstname;
    private String developerLastname;
}
