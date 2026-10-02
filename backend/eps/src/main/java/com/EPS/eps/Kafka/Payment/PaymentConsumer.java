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
        topics="payment-complete",
        groupId="payment-group"
    )    
    public void processPayment(OrderCreatedEvent event){
        Long orderId=event.getOrderId();
        Payment payment=paymentRepository.findByOrderId(orderId)
                        .orElseThrow(()->new RuntimeException("Payment Not Found"));
        if(payment.getStatus()==PaymentStatus.SUCCESS){
            System.out.println("Payment already complete for order : "+orderId);
            return ;
        }
        System.out.println("Processing Payment for order: "+orderId);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        Order order=orderRepository.findById(orderId)
                    .orElseThrow(()->new RuntimeException("Order Not Found"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        System.out.println("Payment Successful For Order: "+orderId);
        paymentProducer.sendPaymentSuccessful(new PaymentSuccessfulEvent(orderId,event.getTotalAmount()));
    }
}
