package com.EPS.eps.DTO;
import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
