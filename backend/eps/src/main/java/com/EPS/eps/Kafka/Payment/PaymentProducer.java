package com.EPS.eps.Kafka.Payment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.PaymentSuccessfulEvent;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PaymentProducer {
    private final KafkaTemplate<String,PaymentSuccessfulEvent>kafkaTemplate;

    public void sendPaymentSuccessful(PaymentSuccessfulEvent event){
        kafkaTemplate.send(
            "payment-successful",
            event.getOrderId().toString(),
            event
        );
    }
}
