ALTER TABLE jobs
ADD COLUMN idempotency_key VARCHAR(255);

CREATE UNIQUE INDEX uq_jobs_idempotency_key
ON jobs (idempotency_key)
WHERE idempotency_key IS NOT NULL;
