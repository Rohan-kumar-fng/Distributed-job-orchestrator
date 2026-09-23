package com.project.distributed_job_orchestrator.Service;

import com.project.distributed_job_orchestrator.Model.JobDefinition;
import com.project.distributed_job_orchestrator.Repository.JobDefinitionRepository;
import com.project.distributed_job_orchestrator.webApi.CreateJobDefinitionRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobDefinitionRepository jobDefinitionRepository;

    public JobService(JobDefinitionRepository jobDefinitionRepository){
        this.jobDefinitionRepository = jobDefinitionRepository;
    }

    public List<JobDefinition> getJobByName(String name){
        List<JobDefinition> jobDefinitions= jobDefinitionRepository.findByName(name);
        return jobDefinitions;
    }

    public Optional<JobDefinition> getJobById(Long id){
        Optional<JobDefinition> jobDefinitions= jobDefinitionRepository.findById(id);
        return jobDefinitions;
    }

    @Transactional
    public void createJob(CreateJobDefinitionRequest jobRequest){
        JobDefinition job = new JobDefinition();
        job.setName(jobRequest.getName());
        job.setCategory(jobRequest.getCategory());
        job.setCronSchedule(jobRequest.getCronSchedule());
        job.setHandleEndpoint(jobRequest.getHandleEndpoint());
        job.setTimeout(jobRequest.getTimeout());
        job.setMaxAttempts(jobRequest.getMaxAttempts());
        job.setCreatedAt(LocalDateTime.now());
        jobDefinitionRepository.save(job);
    }

    @Transactional
    public void deleteJobByName(String name){
        jobDefinitionRepository.deleteByName(name);
    }

}
