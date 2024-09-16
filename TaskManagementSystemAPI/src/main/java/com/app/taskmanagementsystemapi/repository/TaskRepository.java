package com.app.taskmanagementsystemapi.repository;

import com.app.taskmanagementsystemapi.entity.Task;
import com.app.taskmanagementsystemapi.entity.User;
import com.app.taskmanagementsystemapi.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByCreatorId(UUID id);

    List<Task> findTasksByUser(User dev);

}
