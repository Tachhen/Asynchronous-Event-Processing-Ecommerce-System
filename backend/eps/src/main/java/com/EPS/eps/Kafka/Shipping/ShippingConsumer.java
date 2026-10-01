package com.EPS.eps.Kafka.Shipping;

import com.EPS.eps.DTO.Event.PaymentSuccessfulEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ShippingConsumer {

    @KafkaListener(
            topics = "payment-successful",
            groupId = "shipping-group"
    )
    public void processShipping(PaymentSuccessfulEvent event) {

        System.out.println(
                "Shipping started for order: "
                        + event.getOrderId()
        );
    }
}