package com.flowforge.worker.repository;

import com.flowforge.worker.domain.DeadLetterJob;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeadLetterJobRepository extends JpaRepository<DeadLetterJob, UUID> {
}
