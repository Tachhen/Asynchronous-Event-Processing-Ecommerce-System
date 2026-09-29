package com.EPS.eps.Kafka.Shipping;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;

@Service
public class ShippingConsumer {
    @KafkaListener (
        topics="orders",
        groupId = "shipping-group"
    )    
    public void processShipping(OrderCreatedEvent event) {
        System.out.println(
            "Processing shipping for order: "
            + event.getOrderId()
        );
    }
}
