package com.flowforge.worker.repository;

import com.flowforge.worker.domain.JobExecutionEffect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface JobExecutionEffectRepository
        extends JpaRepository<JobExecutionEffect, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO job_execution_effects (
                id,
                job_id,
                effect_key,
                created_at
            )
            VALUES (
                :id,
                :jobId,
                :effectKey,
                :createdAt
            )
            ON CONFLICT (job_id, effect_key) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("id") UUID id,
            @Param("jobId") UUID jobId,
            @Param("effectKey") String effectKey,
            @Param("createdAt") Instant createdAt
    );
}
