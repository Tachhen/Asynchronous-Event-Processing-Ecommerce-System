package com.EPS.eps.Kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class OrderProducer {
    private final KafkaTemplate<String,OrderCreatedEvent>kafkaTemplate;
    public void sendOrderCreated(OrderCreatedEvent event){
        kafkaTemplate.send(
            "orders",
            event.getOrderId().toString(),
            event
        );
    }
}
