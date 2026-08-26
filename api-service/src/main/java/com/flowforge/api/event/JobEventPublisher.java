package com.flowforge.api.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobEventPublisher {

    private static final String TOPIC = "jobs.created";

    private final KafkaTemplate<String, JobCreatedEvent> kafkaTemplate;

    public JobEventPublisher(KafkaTemplate<String, JobCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(JobCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.jobId().toString(),
                event
        );
    }
}
