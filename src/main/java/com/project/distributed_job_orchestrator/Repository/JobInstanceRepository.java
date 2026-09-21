package com.project.distributed_job_orchestrator.Repository;

import com.project.distributed_job_orchestrator.Model.JobInstances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract class JobInstanceRepository implements JpaRepository<Long, JobInstances> {
}
