package com.flowforge.api.controller;

import com.flowforge.api.dto.CreateJobRequest;
import com.flowforge.api.dto.CreateJobResponse;
import com.flowforge.api.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateJobResponse createJob(@RequestBody CreateJobRequest request) {
        return jobService.createJob(request);
    }
}
