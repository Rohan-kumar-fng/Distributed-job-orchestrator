package com.project.distributed_job_orchestrator.Repository;

import com.project.distributed_job_orchestrator.Model.JobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract class JobDefinitionRepository implements JpaRepository<Long, JobDefinition> {

}
