package com.flowforge.worker.repository;

import com.flowforge.worker.domain.JobAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JobAttemptRepository extends JpaRepository<JobAttempt, UUID> {

    Optional<JobAttempt> findByJobIdAndAttemptNumber(
            UUID jobId,
            int attemptNumber
    );
}
