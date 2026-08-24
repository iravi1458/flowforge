package com.flowforge.api.service;

import com.flowforge.api.domain.Job;
import com.flowforge.api.domain.JobStatus;
import com.flowforge.api.dto.CreateJobRequest;
import com.flowforge.api.dto.CreateJobResponse;
import com.flowforge.api.dto.JobResponse;
import com.flowforge.api.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public JobResponse getJob(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow();

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

        return new CreateJobResponse(
                job.getId(),
                job.getStatus()
        );
    }
}
