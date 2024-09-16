package com.app.taskmanagementsystemapi.entity;

import com.app.taskmanagementsystemapi.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_tm")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task extends BaseEntity{
    protected String title;
    protected String description;
    protected LocalDateTime deadline;
    @Enumerated(EnumType.STRING)
    protected TaskStatus taskStatus;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    protected User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    protected Project project;


}
