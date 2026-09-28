package com.EPS.eps.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;

@Service
public class OrderConsumer {
    @KafkaListener(
        topics="orders",
        groupId="order-processing-group"
    )
    public void consumerOrderCreated(OrderCreatedEvent event){
        System.out.println(
            "Recieved orderCreatedEvent for order: "
            +event.getOrderId()
        );
    }
}
