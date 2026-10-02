package com.EPS.eps.Kafka.Payment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.PaymentCompleteEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class PaymentCompleteProducer {
    private final KafkaTemplate<String,PaymentCompleteEvent>kafkaTemplate;

    public void sendPaymentComplete(PaymentCompleteEvent event) {

        kafkaTemplate.send(
            "payment-complete",
            event.getOrderId().toString(),
            event
        );
    }

    public void sendDuplicatePayment(PaymentCompleteEvent event) {

        kafkaTemplate.send(
            "payment-complete",
            event.getOrderId().toString(),
            event
        );

        kafkaTemplate.send(
            "payment-complete",
            event.getOrderId().toString(),
            event
        );
    }
}
