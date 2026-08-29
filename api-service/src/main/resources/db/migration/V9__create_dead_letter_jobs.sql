CREATE TABLE dead_letter_jobs (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    attempt_count INTEGER NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_dead_letter_job UNIQUE (job_id)
);

CREATE INDEX idx_dead_letter_jobs_created_at
    ON dead_letter_jobs(created_at DESC);
