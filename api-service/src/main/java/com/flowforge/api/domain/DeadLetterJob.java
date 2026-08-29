package com.flowforge.api.domain;

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

    public UUID getId() {
        return id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
