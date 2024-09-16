package com.app.taskmanagementsystemapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
   private String id;
   private String title;
   private String projectManagerFirstname;
   private String projectManagerLastname;
   private LocalDateTime createdAt;
   private String creatorId;
}
