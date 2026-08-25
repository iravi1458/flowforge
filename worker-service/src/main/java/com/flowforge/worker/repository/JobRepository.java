package com.flowforge.worker.repository;

import com.flowforge.worker.domain.Job;
import com.flowforge.worker.domain.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    Optional<Job> findFirstByStatusOrderByCreatedAtAsc(JobStatus status);
}
