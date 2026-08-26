package com.flowforge.api.service;

import com.flowforge.api.domain.Job;
import com.flowforge.api.domain.JobStatus;
import com.flowforge.api.dto.CreateJobRequest;
import com.flowforge.api.dto.CreateJobResponse;
import com.flowforge.api.dto.JobResponse;
import com.flowforge.api.event.OutboxEvent;
import com.flowforge.api.exception.JobNotFoundException;
import com.flowforge.api.repository.JobRepository;
import com.flowforge.api.repository.OutboxEventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final OutboxEventRepository outboxEventRepository;

    public JobService(
            JobRepository jobRepository,
            OutboxEventRepository outboxEventRepository
    ) {
        this.jobRepository = jobRepository;
        this.outboxEventRepository = outboxEventRepository;
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

    @Transactional
    public CreateJobResponse createJob(CreateJobRequest request) {
        JobStatus initialStatus =
                request.scheduledAt() == null ? JobStatus.QUEUED : JobStatus.SCHEDULED;

        Job job = new Job(
                UUID.randomUUID(),
                request.jobType(),
                initialStatus,
                request.payload(),
                0,
                request.maxAttempts(),
                Instant.now(),
                request.scheduledAt()
        );

        jobRepository.save(job);

        String eventPayload = """
                {"jobId":"%s","jobType":"%s"}
                """.formatted(job.getId(), job.getJobType()).trim();

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                job.getId(),
                "JOB_CREATED",
                eventPayload,
                Instant.now()
        );

        outboxEventRepository.save(outboxEvent);

        return new CreateJobResponse(
                job.getId(),
                job.getStatus()
        );
    }
}
