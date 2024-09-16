package com.app.taskmanagementsystemapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    protected UUID id;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "creator_id")
    protected UUID creatorId;

    @PrePersist
    public void setCreatedAt() {
        createdAt = LocalDateTime.now();
    }

}
