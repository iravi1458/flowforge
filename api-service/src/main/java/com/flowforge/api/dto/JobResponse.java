package com.flowforge.api.dto;

import com.flowforge.api.domain.JobStatus;
import com.flowforge.api.domain.JobType;

import java.time.Instant;
import java.util.UUID;

public record JobResponse(
    UUID id,
    JobType jobType,
    JobStatus status,
    String payload,
    int attemptCount,
    int maxAttempts,
    Instant createdAt
) {
}
