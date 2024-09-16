package com.app.taskmanagementsystemapi.repository;

import com.app.taskmanagementsystemapi.dto.UserResponse;
import com.app.taskmanagementsystemapi.entity.Project;
import com.app.taskmanagementsystemapi.entity.User;
import com.app.taskmanagementsystemapi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);
    List<User> getAllByRoleAndCreatorIdAndProjectToBeManagedEquals(Role role , UUID id, Project project);
    List<User> getAllByRoleAndCreatorId(Role role , UUID id);
}
