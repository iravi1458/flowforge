package com.flowforge.worker.repository;

import com.flowforge.worker.domain.JobAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobAttemptRepository extends JpaRepository<JobAttempt, UUID> {
}
