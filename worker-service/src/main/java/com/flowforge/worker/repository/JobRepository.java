package com.flowforge.worker.repository;

import com.flowforge.worker.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    @Query(
        value = """
            SELECT *
            FROM jobs
            WHERE status = 'QUEUED'
              AND (next_attempt_at IS NULL OR next_attempt_at <= CURRENT_TIMESTAMP)
              AND (scheduled_at IS NULL OR scheduled_at <= CURRENT_TIMESTAMP)
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<Job> findNextQueuedJobForUpdate();

    @Query(
        value = """
            SELECT *
            FROM jobs
            WHERE status = 'RUNNING'
              AND lease_expires_at IS NOT NULL
              AND lease_expires_at <= CURRENT_TIMESTAMP
            ORDER BY lease_expires_at
            FOR UPDATE SKIP LOCKED
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<Job> findNextExpiredLeaseForUpdate();
}
