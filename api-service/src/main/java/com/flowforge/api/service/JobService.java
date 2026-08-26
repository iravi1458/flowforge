package com.flowforge.api.service;

import com.flowforge.api.domain.Job;
import com.flowforge.api.domain.JobStatus;
import com.flowforge.api.dto.CreateJobRequest;
import com.flowforge.api.dto.CreateJobResponse;
import com.flowforge.api.dto.JobResponse;
import com.flowforge.api.repository.JobRepository;
import com.flowforge.api.exception.JobNotFoundException;
import com.flowforge.api.event.JobCreatedEvent;
import com.flowforge.api.event.JobEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobEventPublisher jobEventPublisher;

    public JobService(
            JobRepository jobRepository,
            JobEventPublisher jobEventPublisher
    ) {
        this.jobRepository = jobRepository;
        this.jobEventPublisher = jobEventPublisher;
    }

    public JobResponse getJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        return new JobResponse(
                job.getId(),
                job.getJobType(),
                job.getStatus(),
                job.getPayload(),
                job.getAttemptCount(),
                job.getMaxAttempts(),
                job.getCreatedAt()
        );
    }

    public CreateJobResponse createJob(CreateJobRequest request) {

        Job job = new Job(
                UUID.randomUUID(),
                request.jobType(),
                JobStatus.QUEUED,
                request.payload(),
                0,
                request.maxAttempts(),
                Instant.now()
        );

        jobRepository.save(job);

        jobEventPublisher.publish(
                new JobCreatedEvent(job.getId(), job.getJobType())
        );

        return new CreateJobResponse(
                job.getId(),
                job.getStatus()
        );
    }
}
