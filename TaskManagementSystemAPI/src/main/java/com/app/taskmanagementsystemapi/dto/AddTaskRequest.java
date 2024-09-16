package com.app.taskmanagementsystemapi.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddTaskRequest {
    private String title;
    private String description;
    private LocalDate deadlineDate;
    private LocalTime deadlineTime;
}
