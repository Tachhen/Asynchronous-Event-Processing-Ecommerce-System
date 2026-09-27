package com.EPS.eps.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.CreateOrderRequest;
import com.EPS.eps.DTO.OrderItemRequest;
import com.EPS.eps.DTO.OrderItemResponse;
import com.EPS.eps.DTO.OrderResponse;
import com.EPS.eps.Entity.Order;
import com.EPS.eps.Entity.OrderItem;
import com.EPS.eps.Entity.OrderStatus;
import com.EPS.eps.Entity.Product;
import com.EPS.eps.Repository.OrderRepository;
import com.EPS.eps.Repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        Order order = new Order();

        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

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

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

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