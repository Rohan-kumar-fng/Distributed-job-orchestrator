package com.project.distributed_job_orchestrator.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    @GetMapping("/health-check")
    public String healthCheck(){
        return "Job Orchestraction Website is Reachable";
    }

}
