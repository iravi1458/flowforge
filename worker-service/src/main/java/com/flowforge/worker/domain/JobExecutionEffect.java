package com.flowforge.worker.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "job_execution_effects",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_job_execution_effect",
                columnNames = {"job_id", "effect_key"}
        )
)
public class JobExecutionEffect {

    @Id
    private UUID id;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "effect_key", nullable = false)
    private String effectKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected JobExecutionEffect() {
    }

    public JobExecutionEffect(
            UUID id,
            UUID jobId,
            String effectKey,
            Instant createdAt
    ) {
        this.id = id;
        this.jobId = jobId;
        this.effectKey = effectKey;
        this.createdAt = createdAt;
    }

    public UUID getJobId() {
        return jobId;
    }

    public String getEffectKey() {
        return effectKey;
    }
}
