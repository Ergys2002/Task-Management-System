package com.app.taskmanagementsystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignProjectRequest {
    private String projectId;
    private String projectManagerId;
}
