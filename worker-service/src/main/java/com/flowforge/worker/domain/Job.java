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

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

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

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void markRunning() {
        this.status = JobStatus.RUNNING;
        this.attemptCount++;
        this.nextAttemptAt = null;
    }

    public void scheduleRetry() {
        long delaySeconds = 5L * (1L << (attemptCount - 1));
        this.status = JobStatus.QUEUED;
        this.nextAttemptAt = Instant.now().plusSeconds(delaySeconds);
    }

    public void markFailed() {
        this.status = JobStatus.FAILED;
        this.nextAttemptAt = null;
    }

    public boolean hasAttemptsRemaining() {
        return this.attemptCount < this.maxAttempts;
    }

    public void markSucceeded() {
        this.status = JobStatus.SUCCEEDED;
        this.nextAttemptAt = null;
    }
}
