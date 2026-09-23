package com.project.distributed_job_orchestrator.Repository;

import com.project.distributed_job_orchestrator.Model.JobDefinition;
import com.project.distributed_job_orchestrator.Model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobDefinitionRepository extends JpaRepository<JobDefinition, Long> {

    List<JobDefinition> findByName(String name);

    void deleteByName(String name);

    @Query("SELECT u from job_definition u where u.name = :name and u.id = :id ORDER BY u.name DESC")
    List<JobDefinition> findByNameAndId(String name, Long id);
}
