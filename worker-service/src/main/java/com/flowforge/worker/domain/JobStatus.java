package com.flowforge.worker.domain;

public enum JobStatus {
    SCHEDULED,
    QUEUED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELLED
}
