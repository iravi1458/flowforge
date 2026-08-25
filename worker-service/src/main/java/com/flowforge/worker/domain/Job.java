package com.flowforge.worker.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Job() {
    }

    public UUID getId() {
        return id;
    }

    public JobType getJobType() {
        return jobType;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markRunning() {
        this.status = JobStatus.RUNNING;
        this.attemptCount++;
    }

    public void markQueued() {
        this.status = JobStatus.QUEUED;
    }

    public void markFailed() {
        this.status = JobStatus.FAILED;
    }

    public boolean hasAttemptsRemaining() {
        return this.attemptCount < this.maxAttempts;
    }

    public void markSucceeded() {
        this.status = JobStatus.SUCCEEDED;
    }
}
