package com.flowforge.api.dto;

import java.time.Instant;
import java.util.UUID;

public record JobAttemptResponse(
        UUID id,
        UUID jobId,
        int attemptNumber,
        String status,
        Instant startedAt,
        Instant finishedAt,
        String errorMessage
) {
}
