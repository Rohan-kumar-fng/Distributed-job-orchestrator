package com.project.distributed_job_orchestrator.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;


// Data is lombook annotation that automaticcaly add the getter, setter, hasCode, toString, All Arg Costructor
@Data
// @Validated // Its meant for @Service/@Controller to enable method-level parameter validation
// Entity is persistance API anotation that is used for table creation using Spring persistance API
@Entity(name = "job_definition")
public class JobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "jobDefinition")
    private List<JobInstances> instances;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false)
    private String cronSchedule;

    @Column(nullable = false)
    private String handleEndpoint;
    private Integer timeout;
    private Integer maxAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  Integer retryTimeInterval;

}
