package com.flowforge.worker.service;

import com.flowforge.worker.domain.Job;
import com.flowforge.worker.domain.JobAttempt;
import com.flowforge.worker.repository.JobAttemptRepository;
import com.flowforge.worker.repository.JobRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobWorker {

    private static final String WORKER_ID = UUID.randomUUID().toString();

    private final JobRepository jobRepository;
    private final JobAttemptRepository jobAttemptRepository;
    private final DeadLetterService deadLetterService;
    private final JobClaimService jobClaimService;
    private final ExecutionIdempotencyService executionIdempotencyService;

    public JobWorker(
            JobRepository jobRepository,
            JobAttemptRepository jobAttemptRepository,
            DeadLetterService deadLetterService,
            JobClaimService jobClaimService,
            ExecutionIdempotencyService executionIdempotencyService
    ) {
        this.jobRepository = jobRepository;
        this.jobAttemptRepository = jobAttemptRepository;
        this.deadLetterService = deadLetterService;
        this.jobClaimService = jobClaimService;
        this.executionIdempotencyService = executionIdempotencyService;
    }

    @Scheduled(fixedDelay = 5000)
    public void processNextJob() {
        jobClaimService.claimNextJob(WORKER_ID)
                .ifPresent(this::processJob);
    }

    private void processJob(Job job) {
        JobAttempt attempt = new JobAttempt(
                UUID.randomUUID(),
                job.getId(),
                job.getAttemptCount(),
                Instant.now()
        );

        jobAttemptRepository.save(attempt);

        System.out.println(
                "[" + Instant.now() + "] Processing job: " + job.getId()
                        + " attempt " + job.getAttemptCount()
        );

        try {
            executeJob(job);

            job.markSucceeded();
            attempt.markSucceeded();

        } catch (RuntimeException e) {

            attempt.markFailed(e.getMessage());

            if (job.hasAttemptsRemaining()) {
                job.scheduleRetry();
                System.out.println("Job failed; re-queued: " + job.getId());
            } else {
                job.markFailed();
                deadLetterService.sendToDeadLetterQueue(job, e.getMessage());
                System.out.println("Job failed permanently and sent to DLQ: " + job.getId());
            }
        }

        jobAttemptRepository.save(attempt);
        jobRepository.save(job);
    }

    private void executeJob(Job job) {
        boolean firstExecution = executionIdempotencyService.claimEffect(
                job.getId(),
                "PRIMARY_EFFECT"
        );

        if (firstExecution) {
            System.out.println("Applied execution effect: " + job.getId());
        } else {
            System.out.println("Skipped duplicate execution effect: " + job.getId());
        }

        if ("force-failure".equals(job.getPayload())) {
            throw new RuntimeException("Simulated job failure");
        }
    }
}
