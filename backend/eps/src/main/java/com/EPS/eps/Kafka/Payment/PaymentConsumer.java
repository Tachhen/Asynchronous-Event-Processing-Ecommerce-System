package com.EPS.eps.Kafka.Payment;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;

@Service
public class PaymentConsumer {
    @KafkaListener(
        topics="orders",
        groupId="payment-group"
    )    
    public void processPayment(OrderCreatedEvent event){
        System.out.println("PROCESSING PAYMENT FOR ORDER: "
                            +event.getOrderId()
                            +" amount:"
                            +event.getTotalAmount()
        );
        throw new RuntimeException("Payment processing failed");
    }
}
