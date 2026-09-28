package com.EPS.eps.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.CreateOrderRequest;
import com.EPS.eps.DTO.OrderItemRequest;
import com.EPS.eps.DTO.OrderItemResponse;
import com.EPS.eps.DTO.OrderResponse;
import com.EPS.eps.DTO.Event.OrderCreatedEvent;
import com.EPS.eps.Entity.Order;
import com.EPS.eps.Entity.OrderItem;
import com.EPS.eps.Entity.OrderStatus;
import com.EPS.eps.Entity.Product;
import com.EPS.eps.Kafka.OrderProducer;
import com.EPS.eps.Repository.OrderRepository;
import com.EPS.eps.Repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class OrderService {
    //Inject dependencies
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderProducer orderProducer;
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        //create a new Order object and add the status and time created also intialize total
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO;
        //Go through all the itemRequest in the CreatedOrderRequest
        for (OrderItemRequest itemRequest : request.getItems()) {
            //make order reference variable and assign it to the product u get by using the productid in products table 
            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            //make orderItem
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setOrder(order);
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(product.getPrice());
            order.getItems().add(item);
            total = total.add(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(itemRequest.getQuantity())
                            )
            );
        }
        //calculate total
        order.setTotalAmount(total);
        //save it to postgres
        Order savedOrder = orderRepository.save(order);
        //initialize orderCreatedEvent from savedOrder details
        OrderCreatedEvent event=new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );
        //pass it to orderProducer and  it is sent to kafka consumer 
        orderProducer.sendOrderCreated(event);
        //response
        return convertToResponse(savedOrder);
    }

    //response which we send back
    private OrderResponse convertToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
}