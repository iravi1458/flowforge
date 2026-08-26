package com.flowforge.worker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_attempts")
public class JobAttempt {

    @Id
    private UUID id;

    private UUID jobId;
    private int attemptNumber;
    private String status;
    private Instant startedAt;
    private Instant finishedAt;
    private String errorMessage;

    protected JobAttempt() {
    }

    public JobAttempt(
            UUID id,
            UUID jobId,
            int attemptNumber,
            Instant startedAt
    ) {
        this.id = id;
        this.jobId = jobId;
        this.attemptNumber = attemptNumber;
        this.status = "RUNNING";
        this.startedAt = startedAt;
    }

    public void markSucceeded() {
        this.status = "SUCCEEDED";
        this.finishedAt = Instant.now();
    }

    public void markFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.finishedAt = Instant.now();
    }
}
