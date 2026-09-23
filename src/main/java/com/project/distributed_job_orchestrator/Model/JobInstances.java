package com.project.distributed_job_orchestrator.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
//@Validated // Its meant for @Service/@Controller to enable method-level parameter validation
@Entity(name = "job_instances")
public class JobInstances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_definition_id", nullable = false)
    private JobDefinition jobDefinition; // This tells spring DAta JPA to create it as foreign key

    @Enumerated(EnumType.STRING) // Reason:- If State is Enum, Hibernate default ordinal integer (0,1,2..)
    private Status status;

    //@NonNull // IT force lombok to create AllArgConstructor, But Hibernate needs NoArgConstrctor for instantiate via Reflection
    @Column(name="payload", nullable = false)
    private String payload;

    // I need to make this idempotencyId as unique
    @Column(name="idempotency_key", unique = true, nullable = false) // On Conflict do nothing, It needs by Client so UUID
    private UUID idempotencyKey;

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
