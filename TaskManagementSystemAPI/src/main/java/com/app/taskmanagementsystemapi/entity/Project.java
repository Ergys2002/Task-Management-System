package com.app.taskmanagementsystemapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_tm")
public class Project extends BaseEntity{
    protected String title;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    protected List<Task> tasks;


    @OneToOne(cascade = CascadeType.MERGE)
    @JsonIgnore
    @JoinColumn(name = "project_manager_id", referencedColumnName = "id")
    private User projectManager;
}
