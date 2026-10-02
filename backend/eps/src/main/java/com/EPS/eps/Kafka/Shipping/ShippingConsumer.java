package com.EPS.eps.Kafka.Shipping;

import com.EPS.eps.DTO.Event.PaymentSuccessfulEvent;
import com.EPS.eps.Entity.Order;
import com.EPS.eps.Entity.OrderStatus;
import com.EPS.eps.Repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class ShippingConsumer {
    private final OrderRepository orderRepository;
    @KafkaListener(
            topics = "payment-successful",
            groupId = "shipping-group"
    )
    public void processShipping(PaymentSuccessfulEvent event) {
        Long orderId = event.getOrderId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
        System.out.println(
                "Shipping started for order: "
                        + orderId
        );
    }
}