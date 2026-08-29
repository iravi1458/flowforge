package com.flowforge.api.controller;

import com.flowforge.api.dto.DeadLetterJobResponse;
import com.flowforge.api.service.DeadLetterJobService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dlq")
public class DeadLetterJobController {

    private final DeadLetterJobService deadLetterJobService;

    public DeadLetterJobController(
            DeadLetterJobService deadLetterJobService
    ) {
        this.deadLetterJobService = deadLetterJobService;
    }

    @GetMapping
    public List<DeadLetterJobResponse> getDeadLetterJobs() {
        return deadLetterJobService.getDeadLetterJobs();
    }
}
