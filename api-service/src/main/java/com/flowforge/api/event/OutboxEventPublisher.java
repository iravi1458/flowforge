package com.flowforge.api.event;

import com.flowforge.api.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        for (OutboxEvent event :
                outboxEventRepository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()) {

            kafkaTemplate.send(
                    "jobs.created",
                    event.getAggregateId().toString(),
                    event.getPayload()
            ).join();

            event.markPublished();
            outboxEventRepository.save(event);
        }
    }
}
