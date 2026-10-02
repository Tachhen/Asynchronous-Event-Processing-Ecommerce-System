package com.EPS.eps.Kafka.Payment;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;
import com.EPS.eps.Entity.Payment;
import com.EPS.eps.Entity.PaymentStatus;
import com.EPS.eps.Repository.PaymentRepository;

import lombok.AllArgsConstructor;

@Service 
@AllArgsConstructor 
public class PaymentPendingConsumer {
    private final PaymentRepository paymentRepository;
    @KafkaListener(
        topics="orders",
        groupId="payment-pending-group"
    )
    public void createPendingPayment(OrderCreatedEvent event){
        Long orderId=event.getOrderId();
        if(paymentRepository.findByOrderId(orderId).isPresent()){
            System.out.println("Payment already exists for order orderID :"+orderId);
            return;
        }
        Payment payment=new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(event.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        System.out.println("Payment created as PENDING for order: "+orderId);
    }
}

