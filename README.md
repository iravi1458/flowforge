# FlowForge

FlowForge is a fault-tolerant distributed workflow and job execution engine.

## Current Features

- Create background jobs through a REST API
- Persist job state in PostgreSQL
- Retrieve jobs by ID
- Validate incoming requests
- Return proper HTTP 404 responses for missing jobs
- Database migrations with Flyway
- Docker Compose setup for PostgreSQL

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker
- Maven

## Current API

### Create a job

`POST /api/v1/jobs`

### Get a job

`GET /api/v1/jobs/{id}`

## Roadmap

- Worker service
- Safe concurrent job claiming
- Retries and failure handling
- Kafka-based dispatch
- Transactional outbox
- Job scheduling
- Lease and crash recovery
- Idempotency
- Redis
- Observability
- Integration tests
- Kubernetes deployment
