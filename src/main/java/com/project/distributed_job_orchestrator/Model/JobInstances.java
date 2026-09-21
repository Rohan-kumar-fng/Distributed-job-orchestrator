package com.project.distributed_job_orchestrator.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Validated
@Entity(name = "job_instances")
public class JobInstances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "job_definition_id")
    private JobDefinition jobDefinition; // This tells spring DAta JPA to create it as foreign key

    private Status status;

    @NonNull
    private String payload;
    private Long idempotencyId;
    private LocalDateTime scheduledAt;
    private String claimedBy;
    private LocalDateTime claimedAt;
    private LocalDateTime lastHeartbeat;
    private Integer attemptCount;
    private LocalDateTime nextRetryAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
