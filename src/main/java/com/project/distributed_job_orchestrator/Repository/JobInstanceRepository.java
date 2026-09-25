package com.project.distributed_job_orchestrator.Repository;

import com.project.distributed_job_orchestrator.Model.JobInstances;
import com.project.distributed_job_orchestrator.Model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobInstanceRepository extends JpaRepository<JobInstances, Long> {

 List<JobInstances> findAllByStatus(Status status);

 List<JobInstances> findAllByIdempotencyKey(UUID id);

 Optional<JobInstances> findByIdempotencyKey(UUID id);

 @Query(value = "Select * from job_instances where status='PENDING' and scheduled_at <= :time FOR UPDATE SKIP LOCKED", nativeQuery = true)
 List<JobInstances> findAllByStatusAndScheduledAt(LocalDateTime time);
}
