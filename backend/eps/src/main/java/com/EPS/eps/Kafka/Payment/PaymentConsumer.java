package com.EPS.eps.Kafka.Payment;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;
import com.EPS.eps.DTO.Event.PaymentSuccessfulEvent;
import com.EPS.eps.Entity.Order;
import com.EPS.eps.Entity.OrderStatus;
import com.EPS.eps.Entity.Payment;
import com.EPS.eps.Entity.PaymentStatus;
import com.EPS.eps.Repository.OrderRepository;
import com.EPS.eps.Repository.PaymentRepository;


import lombok.RequiredArgsConstructor;
import tools.jackson.core.Base64Variant.PaddingReadBehaviour;

@Service
@RequiredArgsConstructor 
public class PaymentConsumer {
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final OrderRepository orderRepository;
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
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        Order order=orderRepository.findById(orderId)
                    .orElseThrow(()->new RuntimeException("Order Not Found"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        System.out.println("Payment Successful For Order: "+orderId);
        paymentProducer.sendPaymentSuccessful(new PaymentSuccessfulEvent(orderId,event.getTotalAmount()));
    }
}
