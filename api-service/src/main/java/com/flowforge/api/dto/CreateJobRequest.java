package com.flowforge.api.dto;

import com.flowforge.api.domain.JobType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateJobRequest(

        @NotNull
        JobType jobType,

        String payload,

        @Min(1)
        @Max(10)
        int maxAttempts,

        Instant scheduledAt

) {
}
