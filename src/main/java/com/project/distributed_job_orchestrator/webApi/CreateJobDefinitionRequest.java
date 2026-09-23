package com.project.distributed_job_orchestrator.webApi;

import lombok.Data;

@Data
public class CreateJobDefinitionRequest {

    private String name;
    private String category;
    private String cronSchedule;
    private String handleEndpoint;
    private Integer timeout;
    private Integer maxAttempts;

    // I think these are fine to job creation.
}
