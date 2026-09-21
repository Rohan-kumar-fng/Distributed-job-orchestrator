package com.project.distributed_job_orchestrator.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;


// Data is lombook annotation that automaticcaly add the getter, setter, hasCode, toString, All Arg Costructor
@Data
@Validated
// Entity is persistance API anotation that is used for table creation using Spring persistance API
@Entity(name = "job_definition")
public class JobDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "jobDefinition")
    private List<JobInstances> instances;

    @NonNull
    private String name;

    private String category;

    @NonNull
    private String cronSchedule;

    @NonNull
    private String handleEndpoint;
    private Integer timeout;
    private Integer maxAttempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
