package com.flowforge.api.service;

import com.flowforge.api.dto.DeadLetterJobResponse;
import com.flowforge.api.repository.DeadLetterJobRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeadLetterJobService {

    private final DeadLetterJobRepository deadLetterJobRepository;

    public DeadLetterJobService(
            DeadLetterJobRepository deadLetterJobRepository
    ) {
        this.deadLetterJobRepository = deadLetterJobRepository;
    }

    public List<DeadLetterJobResponse> getDeadLetterJobs() {
        return deadLetterJobRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(job -> new DeadLetterJobResponse(
                        job.getId(),
                        job.getJobId(),
                        job.getAttemptCount(),
                        job.getErrorMessage(),
                        job.getCreatedAt()
                ))
                .toList();
    }
}
