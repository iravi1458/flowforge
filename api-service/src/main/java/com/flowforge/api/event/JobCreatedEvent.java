package com.flowforge.api.event;

import com.flowforge.api.domain.JobType;

import java.util.UUID;

public record JobCreatedEvent(
        UUID jobId,
        JobType jobType
) {
}
