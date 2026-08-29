package com.flowforge.worker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_jobs")
public class DeadLetterJob {

    @Id
    private UUID id;

    private UUID jobId;
    private int attemptCount;
    private String errorMessage;
    private Instant createdAt;

    protected DeadLetterJob() {
    }

    public DeadLetterJob(
            UUID id,
            UUID jobId,
            int attemptCount,
            String errorMessage,
            Instant createdAt
    ) {
        this.id = id;
        this.jobId = jobId;
        this.attemptCount = attemptCount;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }
}
