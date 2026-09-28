package com.EPS.eps.DTO.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;


//represents that an order was created
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class OrderCreatedEvent {
    private Long orderId;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
