package com.flowforge.worker.service;

import com.flowforge.worker.domain.DeadLetterJob;
import com.flowforge.worker.domain.Job;
import com.flowforge.worker.event.JobDlqPublisher;
import com.flowforge.worker.repository.DeadLetterJobRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class DeadLetterService {

    private final DeadLetterJobRepository deadLetterJobRepository;
    private final JobDlqPublisher jobDlqPublisher;

    public DeadLetterService(
            DeadLetterJobRepository deadLetterJobRepository,
            JobDlqPublisher jobDlqPublisher
    ) {
        this.deadLetterJobRepository = deadLetterJobRepository;
        this.jobDlqPublisher = jobDlqPublisher;
    }

    @Transactional
    public void sendToDeadLetterQueue(Job job, String errorMessage) {
        DeadLetterJob deadLetterJob = new DeadLetterJob(
                UUID.randomUUID(),
                job.getId(),
                job.getAttemptCount(),
                errorMessage,
                Instant.now()
        );

        deadLetterJobRepository.save(deadLetterJob);
        jobDlqPublisher.publish(job, errorMessage);
    }
}
