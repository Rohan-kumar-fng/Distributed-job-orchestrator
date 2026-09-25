package com.project.distributed_job_orchestrator.Service;

import com.project.distributed_job_orchestrator.Model.JobDefinition;
import com.project.distributed_job_orchestrator.Model.JobInstances;
import com.project.distributed_job_orchestrator.Model.Status;
import com.project.distributed_job_orchestrator.Repository.JobDefinitionRepository;
import com.project.distributed_job_orchestrator.Repository.JobInstanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PullJobService {
    JobInstanceRepository jobInstanceRepository;
    JobDefinitionRepository jobDefinitionRepository;

    public PullJobService(JobInstanceRepository jobInstanceRepository, JobDefinitionRepository jobDefinitionRepository){
        this.jobInstanceRepository = jobInstanceRepository;
        this.jobDefinitionRepository = jobDefinitionRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public Boolean pullJobToRun() throws RuntimeException{

        try {
            List<JobInstances> jobInstances = jobInstanceRepository.findAllByStatusAndScheduledAt(LocalDateTime.now());
            for(JobInstances job: jobInstances){
                JobDefinition jobDefinition = jobDefinitionRepository.findById(job.getJobDefinition().getId()).orElseThrow(() -> new NoSuchElementException("No such Job Id present in Job definition table"));
                if(!validate(job,jobDefinition)){
                    System.out.println("This job is not Ready to be scheduled");
                    continue;
                }
                run(job,jobDefinition);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public Boolean validate(JobInstances job, JobDefinition jobDefinition){
        if(job.getAttemptCount() < jobDefinition.getMaxAttempts()){
            return true;
        }
        return false;
    }

    public void run(JobInstances job, JobDefinition jobDefinition){
        job.setStatus(Status.CLAIMED);
        job.setClaimedBy(Thread.currentThread().getName());
        job.setClaimedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        // Create the API and Hit the payload
        try{
            executeJobAPI(job, jobDefinition);
        } catch (Exception e) {
            addJobOnRetry(job, jobDefinition);
        }

        jobInstanceRepository.save(job); // Saving for making it transactional

    }

    public void executeJobAPI(JobInstances job, JobDefinition jobDefinition) throws RuntimeException,NullPointerException, URISyntaxException {
        job.setStartedAt(LocalDateTime.now());
        // How to create POST request from this job information
        String handleEndpoint = jobDefinition.getHandleEndpoint();
        try {
            HttpRequest httpRequest = HttpRequest
                    .newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(job.getPayload()))
                    .header("Content-Type","application/json")
                    .uri(new URI(handleEndpoint))
                    .timeout(Duration.ofSeconds(jobDefinition.getTimeout()))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest,HttpResponse.BodyHandlers.ofString());
            // Maybe this line needs to wait till we get some response back
            // I remember some joinThread method but need to check is this happening in new thread
            if(response.statusCode() != 200){
                throw new RuntimeException("Job Not successful, Retrying!!");
            }
            job.setFinishedAt(LocalDateTime.now());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Transactional
    public void addJobOnRetry(JobInstances job, JobDefinition jobDefinition){
        job.setStatus(Status.RETRYING);
        job.setAttemptCount(job.getAttemptCount()+1);
        job.setUpdatedAt(LocalDateTime.now());
        job.setClaimedBy(null);
        job.setClaimedAt(null);
        job.setNextRetryAt(LocalDateTime.now().plusSeconds(jobDefinition.getRetryTimeInterval()));
        job.setScheduledAt(LocalDateTime.now().plusSeconds(jobDefinition.getRetryTimeInterval()));

        jobInstanceRepository.save(job);

    }
}
