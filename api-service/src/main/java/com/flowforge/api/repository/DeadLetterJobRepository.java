package com.flowforge.api.repository;

import com.flowforge.api.domain.DeadLetterJob;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeadLetterJobRepository extends JpaRepository<DeadLetterJob, UUID> {

    List<DeadLetterJob> findTop50ByOrderByCreatedAtDesc();
}
