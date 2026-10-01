package com.EPS.eps.Kafka.Notification;

import com.EPS.eps.DTO.Event.PaymentSuccessfulEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(
            topics = "payment-successful",
            groupId = "notification-group"
    )
    public void sendNotification(PaymentSuccessfulEvent event) {

        System.out.println(
                "Payment notification sent for order: "
                        + event.getOrderId()
                        + " | Amount: "
                        + event.getAmount()
        );
    }
}