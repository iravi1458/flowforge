package com.flowforge.worker.service;

import com.flowforge.worker.repository.JobExecutionEffectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ExecutionIdempotencyService {

    private final JobExecutionEffectRepository repository;

    public ExecutionIdempotencyService(
            JobExecutionEffectRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public boolean claimEffect(UUID jobId, String effectKey) {
        int inserted = repository.insertIfAbsent(
                UUID.randomUUID(),
                jobId,
                effectKey,
                Instant.now()
        );

        return inserted == 1;
    }
}
