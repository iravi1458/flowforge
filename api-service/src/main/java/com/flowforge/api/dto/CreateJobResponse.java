package com.flowforge.api.dto;

import com.flowforge.api.domain.JobStatus;

import java.util.UUID;

public record CreateJobResponse(
    UUID jobId,
    JobStatus status
) {
}
