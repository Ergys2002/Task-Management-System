package com.app.taskmanagementsystemapi.repository;

import com.app.taskmanagementsystemapi.entity.Project;
import com.app.taskmanagementsystemapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Project findProjectByTitle(String titile);
    Project findProjectByProjectManager(User projectManager);
    List<Project> findProjectsByCreatorId(UUID id);
}
