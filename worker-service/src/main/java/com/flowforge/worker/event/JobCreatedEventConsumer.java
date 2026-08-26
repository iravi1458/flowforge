package com.flowforge.worker.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobCreatedEventConsumer {

    @KafkaListener(topics = "jobs.created")
    public void consume(JobCreatedEvent event) {
        System.out.println("Received Kafka job event: " + event);
    }
}
