package com.flowforge.worker.service;

import com.flowforge.worker.domain.Job;
import com.flowforge.worker.domain.JobStatus;
import com.flowforge.worker.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobWorker {

    private final JobRepository jobRepository;

    public JobWorker(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
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

        System.out.println(
                "[" + java.time.Instant.now() + "] Processing job: " + job.getId()
                        + " attempt " + job.getAttemptCount()
        );

        try {
            executeJob(job);
            job.markSucceeded();
        } catch (RuntimeException e) {
            if (job.hasAttemptsRemaining()) {
                job.scheduleRetry();
                System.out.println("Job failed; re-queued: " + job.getId());
            } else {
                job.markFailed();
                System.out.println("Job failed permanently: " + job.getId());
            }
        }

        jobRepository.save(job);
    }

    private void executeJob(Job job) {
        if ("force-failure".equals(job.getPayload())) {
            throw new RuntimeException("Simulated job failure");
        }
    }

}
