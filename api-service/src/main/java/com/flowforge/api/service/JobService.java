package com.flowforge.api.service;

import com.flowforge.api.domain.Job;
import com.flowforge.api.dto.CreateJobRequest;
import com.flowforge.api.dto.CreateJobResponse;
import com.flowforge.api.dto.JobAttemptResponse;
import com.flowforge.api.dto.JobResponse;
import com.flowforge.api.exception.JobNotFoundException;
import com.flowforge.api.repository.JobAttemptRepository;
import com.flowforge.api.repository.JobRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobAttemptRepository jobAttemptRepository;
    private final JobCreationService jobCreationService;

    public JobService(
            JobRepository jobRepository,
            JobAttemptRepository jobAttemptRepository,
            JobCreationService jobCreationService
    ) {
        this.jobRepository = jobRepository;
        this.jobAttemptRepository = jobAttemptRepository;
        this.jobCreationService = jobCreationService;
    }

    public List<JobResponse> getJobs() {
        return jobRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(job -> new JobResponse(
                        job.getId(),
                        job.getJobType(),
                        job.getStatus(),
                        job.getPayload(),
                        job.getAttemptCount(),
                        job.getMaxAttempts(),
                        job.getCreatedAt()
                ))
                .toList();
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

    public List<JobAttemptResponse> getJobAttempts(UUID id) {
        if (!jobRepository.existsById(id)) {
            throw new JobNotFoundException(id);
        }

        return jobAttemptRepository.findByJobIdOrderByAttemptNumberAsc(id)
                .stream()
                .map(attempt -> new JobAttemptResponse(
                        attempt.getId(),
                        attempt.getJobId(),
                        attempt.getAttemptNumber(),
                        attempt.getStatus(),
                        attempt.getStartedAt(),
                        attempt.getFinishedAt(),
                        attempt.getErrorMessage()
                ))
                .toList();
    }

    public CreateJobResponse createJob(
            CreateJobRequest request,
            String idempotencyKey
    ) {
        String normalizedIdempotencyKey =
                idempotencyKey == null || idempotencyKey.isBlank()
                        ? null
                        : idempotencyKey.trim();

        if (normalizedIdempotencyKey != null) {
            var existingJob =
                    jobRepository.findByIdempotencyKey(normalizedIdempotencyKey);

            if (existingJob.isPresent()) {
                Job job = existingJob.get();

                return new CreateJobResponse(
                        job.getId(),
                        job.getStatus()
                );
            }
        }

        try {
            return jobCreationService.createNewJob(
                    request,
                    normalizedIdempotencyKey
            );
        } catch (DataIntegrityViolationException exception) {
            if (normalizedIdempotencyKey == null) {
                throw exception;
            }

            Job existingJob = jobRepository
                    .findByIdempotencyKey(normalizedIdempotencyKey)
                    .orElseThrow(() -> exception);

            return new CreateJobResponse(
                    existingJob.getId(),
                    existingJob.getStatus()
            );
        }
    }
}
