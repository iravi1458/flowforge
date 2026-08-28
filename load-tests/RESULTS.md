# FlowForge Load Test Results

## Environment

- Local macOS development machine
- Java 21
- Spring Boot 4.1.1
- PostgreSQL 17
- k6 2.2.0
- Endpoint: `GET /api/v1/jobs/{id}`

## Results

| Test | Throughput | Avg Latency | p95 Latency | Failure Rate |
|---|---:|---:|---:|---:|
| 20 VUs | 92.8 req/s | 14.18 ms | 20.77 ms | 0% |
| 50 VUs | 242.0 req/s | 6.10 ms | 11.74 ms | 0% |
| 100 VUs | 467.2 req/s | 11.83 ms | 41.37 ms | 0% |
| 200 VUs, immediate | 974.2 req/s | 4.70 ms | 13.60 ms | 0.32% |
| 200 VUs, gradual ramp | 772.1 req/s average | 6.36 ms | 21.87 ms | 0% |

## Analysis

FlowForge scaled cleanly through 100 concurrent virtual users with zero request failures.

An instantaneous jump from 0 to 200 VUs produced a small number of TCP
`connection reset by peer` errors. The application continued serving successful
requests with low latency.

Repeating the test with a gradual ramp to 200 VUs eliminated the failures
entirely while maintaining low latency. This indicates the earlier failures
were associated with the abrupt local connection burst rather than sustained
application or PostgreSQL saturation.

These numbers represent a local development benchmark and should not be
interpreted as production capacity measurements.
