package com.project.distributed_job_orchestrator.Controller;

import com.project.distributed_job_orchestrator.Model.JobDefinition;
import com.project.distributed_job_orchestrator.Model.JobInstances;
import com.project.distributed_job_orchestrator.Model.Status;
import com.project.distributed_job_orchestrator.Service.JobInstanceService;
import com.project.distributed_job_orchestrator.Service.JobService;
import com.project.distributed_job_orchestrator.webApi.CreateJobDefinitionRequest;
import com.project.distributed_job_orchestrator.webApi.SubmitJobInstanceRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;
    private final JobInstanceService jobInstanceService;

    public JobController(JobService jobService, JobInstanceService jobInstanceService){
        this.jobService = jobService;
        this.jobInstanceService = jobInstanceService;
    }

    @GetMapping("/health-check")
    public String healthCheck(){
        return "Job Orchestraction Website is Reachable";
    }

    @GetMapping("/jobs/name={name}")
    public ResponseEntity<List<JobDefinition>> getJobByName(@PathVariable String name){
        List<JobDefinition> jobDefinitionList = jobService.getJobByName(name);
        return ResponseEntity.ok().body(jobDefinitionList);
    }

    @GetMapping("/jobs/id={id}")
    public ResponseEntity<JobDefinition> getJobById(@PathVariable Long id){
        return jobService.getJobById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); // Way to handle optional
    }

    @PostMapping("/jobs")
    public ResponseEntity<String> createJob(@RequestBody CreateJobDefinitionRequest job){
        jobService.createJob(job);
        return ResponseEntity.status(HttpStatus.CREATED).body("Jobs created");
    }

    @DeleteMapping("/jobs/name={name}")
    public ResponseEntity<String> deleteJob(@PathVariable String name){
        jobService.deleteJobByName(name);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Job deleted");
    }

    @GetMapping("/jobs/status={status}")
    public ResponseEntity<List<JobInstances>> getJobInstancesByStatus(@PathVariable Status status){
        List<JobInstances> jobsIns = jobInstanceService.findAllInstancesByStatus(status);
        return ResponseEntity.ok(jobsIns);
    }

    @PostMapping("/job/submit")
    public ResponseEntity<String> submitJobInstance(@RequestBody SubmitJobInstanceRequest request){
        Optional<JobInstances> existingJobInstances = jobInstanceService.findInstancesByIdempotencyKey(request.getIdempotencyKey());
        // Need to handle the concurrency for this if condition
        if(existingJobInstances.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Job Instance already present");
        }
        jobInstanceService.submitJobInstance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Job Instance Created");
    }






}
