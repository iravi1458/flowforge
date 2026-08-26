package com.flowforge.scheduler.service;

import com.flowforge.scheduler.domain.Job;
import com.flowforge.scheduler.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobScheduler {

    private final JobRepository jobRepository;

    public JobScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void releaseDueJob() {
        jobRepository.findNextDueScheduledJobForUpdate()
                .ifPresent(this::markQueued);
    }

    private void markQueued(Job job) {
        job.markQueued();
        jobRepository.save(job);

        System.out.println("Released scheduled job: " + job.getId());
    }
}
