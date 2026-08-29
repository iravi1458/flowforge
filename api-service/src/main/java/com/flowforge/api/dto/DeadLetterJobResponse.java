package com.flowforge.api.dto;

import java.time.Instant;
import java.util.UUID;

public record DeadLetterJobResponse(
        UUID id,
        UUID jobId,
        int attemptCount,
        String errorMessage,
        Instant createdAt
) {
}
