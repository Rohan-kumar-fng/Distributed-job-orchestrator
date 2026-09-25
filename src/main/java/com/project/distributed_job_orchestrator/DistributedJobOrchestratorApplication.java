package com.project.distributed_job_orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DistributedJobOrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedJobOrchestratorApplication.class, args);
	}

}
