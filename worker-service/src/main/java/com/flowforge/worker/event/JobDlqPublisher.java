package com.flowforge.worker.event;

import com.flowforge.worker.domain.Job;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobDlqPublisher {

    private static final String TOPIC = "jobs.dlq";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public JobDlqPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Job job, String errorMessage) {
        String payload = """
                {"jobId":"%s","attemptCount":%d,"errorMessage":"%s"}
                """.formatted(
                job.getId(),
                job.getAttemptCount(),
                errorMessage
        ).trim();

        kafkaTemplate.send(
                TOPIC,
                job.getId().toString(),
                payload
        ).join();
    }
}
