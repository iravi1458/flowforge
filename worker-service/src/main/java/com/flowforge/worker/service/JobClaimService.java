package com.flowforge.worker.service;

import com.flowforge.worker.domain.Job;
import com.flowforge.worker.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class JobClaimService {

    private static final long LEASE_SECONDS = 30;

    private final JobRepository jobRepository;

    public JobClaimService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Optional<Job> claimNextJob(String workerId) {
        Optional<Job> job = jobRepository.findNextQueuedJobForUpdate();

        job.ifPresent(j -> {
            j.markRunning(
                    workerId,
                    Instant.now().plusSeconds(LEASE_SECONDS)
            );
            jobRepository.save(j);
        });

        return job;
    }
}
