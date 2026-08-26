package com.flowforge.scheduler.repository;

import com.flowforge.scheduler.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    @Query(
        value = """
            SELECT *
            FROM jobs
            WHERE status = 'SCHEDULED'
              AND scheduled_at <= CURRENT_TIMESTAMP
            ORDER BY scheduled_at
            FOR UPDATE SKIP LOCKED
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<Job> findNextDueScheduledJobForUpdate();
}
