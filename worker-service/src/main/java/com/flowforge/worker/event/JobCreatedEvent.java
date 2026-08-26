package com.flowforge.worker.event;

import java.util.UUID;

public record JobCreatedEvent(
        UUID jobId,
        String jobType
) {
}
