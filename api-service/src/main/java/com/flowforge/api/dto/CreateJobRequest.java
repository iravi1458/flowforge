package com.flowforge.api.dto;

import com.flowforge.api.domain.JobType;

public record CreateJobRequest(
    JobType jobType,
    String payload,
    int maxAttempts
) {
}
