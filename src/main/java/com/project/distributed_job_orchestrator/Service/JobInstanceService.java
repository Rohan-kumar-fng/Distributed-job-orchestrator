package com.project.distributed_job_orchestrator.Service;

import com.project.distributed_job_orchestrator.Model.JobDefinition;
import com.project.distributed_job_orchestrator.Model.JobInstances;
import com.project.distributed_job_orchestrator.Model.Status;
import com.project.distributed_job_orchestrator.Repository.JobDefinitionRepository;
import com.project.distributed_job_orchestrator.Repository.JobInstanceRepository;
import com.project.distributed_job_orchestrator.webApi.SubmitJobInstanceRequest;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobInstanceService {
    private final JobInstanceRepository jobInstanceRepository;
    private final JobDefinitionRepository jobDefinitionRepository;

    public JobInstanceService(JobInstanceRepository jobInstanceRepository, JobDefinitionRepository jobDefinitionRepository){
        this.jobInstanceRepository = jobInstanceRepository;
        this.jobDefinitionRepository = jobDefinitionRepository;
    }

    public List<JobInstances> findAllInstancesByStatus(Status status){
        return jobInstanceRepository.findAllByStatus(status);
    }

    public Optional<JobInstances> findInstancesByIdempotencyKey(UUID id){
        return jobInstanceRepository.findByIdempotencyKey(id);
    }

    public JobInstances submitJobInstance(SubmitJobInstanceRequest request) throws DataIntegrityViolationException{
        // I need to check the idempotnecy key concept here
        // First extract the jobDefinition with that id
        // Idempotency Key concept says that if that UUID is already present before please ignore it
        // Other wise insert it
        // How to extract this Key

        // Way1
//        List<JobInstances> currentInstances = jobInstanceRepository.findAllByIdempotencyKey(request.getIdempotencyKey());
//        if(currentInstances.isEmpty()){
            // This is not present before, Add this in instance table
            JobInstances jobInstances = new JobInstances();
            jobInstances.setIdempotencyKey(request.getIdempotencyKey());
            jobInstances.setPayload(request.getPayload());
            jobInstances.setCreatedAt(LocalDateTime.now());
            jobInstances.setStatus(Status.PENDING);
            jobInstances.setJobDefinition(jobDefinitionRepository.findById(request.getJobDefinitionId()).orElseThrow(() -> new NoSuchElementException("No job definition with id " + request.getJobDefinitionId())));
            jobInstances.setAttemptCount(0);
            jobInstances.setScheduledAt(nextScheduleTime(request.getJobDefinitionId()));

//            jobInstanceRepository.save(jobInstances);
//        }

        // Way2 (Anywhy you need to add it in db, it will throw error if already ezist

        try {
            return jobInstanceRepository.save(jobInstances);
        } catch (DataIntegrityViolationException e){
            return jobInstanceRepository.findByIdempotencyKey(request.getIdempotencyKey()).orElseThrow(() -> new RuntimeException(e));
        }

    }

    private LocalDateTime nextScheduleTime(Long id){
        JobDefinition jobDefinition = jobDefinitionRepository.findById(id).orElseThrow(() -> new RuntimeException("Id Not found"));
        String cron = jobDefinition.getCronSchedule();
        CronExpression cronExpression = CronExpression.parse(cron);
        return cronExpression.next(LocalDateTime.now());

        // Add all the scheduled for next one day

    }
}
