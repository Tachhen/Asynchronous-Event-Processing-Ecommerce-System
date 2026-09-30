package com.EPS.eps.Kafka.Payment;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;
import com.EPS.eps.Entity.Payment;
import com.EPS.eps.Entity.PaymentStatus;
import com.EPS.eps.Repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class PaymentConsumer {
    private final PaymentRepository paymentRepository;
    @KafkaListener(
        topics="orders",
        groupId="payment-group"
    )    
    public void processPayment(OrderCreatedEvent event){
        Long orderId=event.getOrderId();
        if(paymentRepository.findByOrderId(orderId).isPresent()){
            System.out.println("Payment already processed for order: "+orderId);
            return;
        }
        System.out.println("Processing Payment for order : "+orderId+" amount: "+event.getTotalAmount());
        Payment payment =new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(event.getTotalAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        System.out.println("Payment Successful For Order: "+orderId);
    }
}
