package com.batistell.inventoryapi.service;

import com.batistell.inventoryapi.model.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final InventoryService inventoryService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false"
    )
    @KafkaListener(topics = "catalog.product.lifecycle", groupId = "inventory-api-group")
    public void consumeProductEvent(ProductEvent event, 
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received message from topic={}, offset={}, action={}, productId={}", 
                topic, offset, event.getAction(), event.getProductId());

        try {
            if ("CREATED".equalsIgnoreCase(event.getAction()) || "CREATE".equalsIgnoreCase(event.getAction())) {
                log.info("Processing creation of inventory for new product: {}", event.getProductId());
                inventoryService.createInventoryForProduct(event.getProductId());
            } else {
                log.info("Ignored action {} for productId={}", event.getAction(), event.getProductId());
            }
        } catch (Exception e) {
            log.error("Error processing message for productId={}", event.getProductId(), e);
            throw e; // throw exception to trigger Spring Kafka retry mechanisms
        }
    }
}
