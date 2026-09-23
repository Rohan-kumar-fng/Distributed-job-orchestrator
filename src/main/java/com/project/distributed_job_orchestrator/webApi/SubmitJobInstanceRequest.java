package com.project.distributed_job_orchestrator.webApi;

import lombok.Data;

import java.util.UUID;

@Data
public class SubmitJobInstanceRequest {

    private Long jobDefinitionId;
    private UUID idempotencyKey;
    private String payload;
}
