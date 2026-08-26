package com.flowforge.worker.service;

import com.flowforge.worker.domain.Job;
import com.flowforge.worker.domain.JobAttempt;
import com.flowforge.worker.event.JobDlqPublisher;
import com.flowforge.worker.repository.JobAttemptRepository;
import com.flowforge.worker.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobRecoveryService {

    private final JobRepository jobRepository;
    private final JobAttemptRepository jobAttemptRepository;
    private final JobDlqPublisher jobDlqPublisher;

    public JobRecoveryService(
            JobRepository jobRepository,
            JobAttemptRepository jobAttemptRepository,
            JobDlqPublisher jobDlqPublisher
    ) {
        this.jobRepository = jobRepository;
        this.jobAttemptRepository = jobAttemptRepository;
        this.jobDlqPublisher = jobDlqPublisher;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void recoverExpiredLease() {
        jobRepository.findNextExpiredLeaseForUpdate()
                .ifPresent(this::recoverJob);
    }

    private void recoverJob(Job job) {
        jobAttemptRepository
                .findByJobIdAndAttemptNumber(
                        job.getId(),
                        job.getAttemptCount()
                )
                .ifPresent(attempt -> markAttemptFailed(attempt));

        if (job.hasAttemptsRemaining()) {
            job.scheduleRetry();
            System.out.println("Recovered expired job lease: " + job.getId());
        } else {
            job.markFailed();
            jobDlqPublisher.publish(job, "Worker lease expired");
            System.out.println("Expired job exhausted all attempts and sent to DLQ: " + job.getId());
        }

        jobRepository.save(job);
    }

    private void markAttemptFailed(JobAttempt attempt) {
        attempt.markFailed("Worker lease expired");
        jobAttemptRepository.save(attempt);
    }
}
