package com.flowforge.api;

import com.flowforge.api.service.RateLimitService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(properties = {
        "management.tracing.export.otlp.enabled=false",
        "management.otlp.metrics.export.enabled=false"
})
@AutoConfigureMockMvc
class ApiServiceApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RateLimitService rateLimitService;

    @Test
    void createAndRetrieveJob() throws Exception {
        when(rateLimitService.isAllowed(
                anyString(),
                anyInt(),
                any()
        )).thenReturn(true);

        String response = mockMvc.perform(post("/api/v1/jobs")
                        .header("Idempotency-Key", "integration-test-key")
                        .contentType("application/json")
                        .content("""
                                {
                                  "jobType": "GENERATE_REPORT",
                                  "payload": "integration-test",
                                  "maxAttempts": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jobId = new tools.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("jobId")
                .asText();

        mockMvc.perform(get("/api/v1/jobs/{id}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobId))
                .andExpect(jsonPath("$.jobType").value("GENERATE_REPORT"))
                .andExpect(jsonPath("$.status").value("QUEUED"))
                .andExpect(jsonPath("$.payload").value("integration-test"))
                .andExpect(jsonPath("$.attemptCount").value(0))
                .andExpect(jsonPath("$.maxAttempts").value(3));
    }
}
