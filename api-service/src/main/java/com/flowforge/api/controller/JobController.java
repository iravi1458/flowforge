package com.flowforge.api.controller;

import com.flowforge.api.dto.CreateJobRequest;
import com.flowforge.api.dto.CreateJobResponse;
import com.flowforge.api.dto.JobResponse;
import com.flowforge.api.dto.JobAttemptResponse;
import com.flowforge.api.service.JobService;
import com.flowforge.api.service.RateLimitService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private static final int CREATE_JOB_LIMIT = 10;
    private static final Duration CREATE_JOB_WINDOW = Duration.ofMinutes(1);

    private final JobService jobService;
    private final RateLimitService rateLimitService;

    public JobController(
            JobService jobService,
            RateLimitService rateLimitService
    ) {
        this.jobService = jobService;
        this.rateLimitService = rateLimitService;
    }

    @GetMapping
    public List<JobResponse> getJobs() {
        return jobService.getJobs();
    }

    @GetMapping("/{id}/attempts")
    public List<JobAttemptResponse> getJobAttempts(@PathVariable UUID id) {
        return jobService.getJobAttempts(id);
    }

    @GetMapping("/{id}")
    public JobResponse getJob(@PathVariable UUID id) {
        return jobService.getJob(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateJobResponse createJob(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateJobRequest request,
            HttpServletRequest servletRequest
    ) {
        String clientIp = servletRequest.getRemoteAddr();
        String rateLimitKey = "rate-limit:jobs:create:" + clientIp;

        if (!rateLimitService.isAllowed(
                rateLimitKey,
                CREATE_JOB_LIMIT,
                CREATE_JOB_WINDOW
        )) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Job creation rate limit exceeded"
            );
        }

        return jobService.createJob(request, idempotencyKey);
    }
}
