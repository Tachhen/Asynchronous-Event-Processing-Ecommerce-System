package com.EPS.eps.Kafka.Notification;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;

@Service
public class NotificationConsumer {
    @KafkaListener (
        topics="orders",
        groupId="notification-group"
    )
    public void sendNotification(OrderCreatedEvent event) {

        System.out.println(
            "Sending notification for order: "
            + event.getOrderId()
        );
    }
}
