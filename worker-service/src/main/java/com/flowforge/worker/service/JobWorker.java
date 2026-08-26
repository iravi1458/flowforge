package com.flowforge.worker.service;

import com.flowforge.worker.domain.Job;
import com.flowforge.worker.domain.JobAttempt;
import com.flowforge.worker.repository.JobAttemptRepository;
import com.flowforge.worker.repository.JobRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobWorker {

    private final JobRepository jobRepository;
    private final JobAttemptRepository jobAttemptRepository;

    public JobWorker(
            JobRepository jobRepository,
            JobAttemptRepository jobAttemptRepository
    ) {
        this.jobRepository = jobRepository;
        this.jobAttemptRepository = jobAttemptRepository;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processNextJob() {
        jobRepository.findNextQueuedJobForUpdate()
                .ifPresent(this::processJob);
    }

    private void processJob(Job job) {
        job.markRunning();
        jobRepository.save(job);

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
                System.out.println("Job failed permanently: " + job.getId());
            }
        }

        jobAttemptRepository.save(attempt);
        jobRepository.save(job);
    }

    private void executeJob(Job job) {
        if ("force-failure".equals(job.getPayload())) {
            throw new RuntimeException("Simulated job failure");
        }
    }
}
