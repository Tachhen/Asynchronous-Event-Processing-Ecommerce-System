package com.EPS.eps.DTO;
import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
//making an orderItem for an order 
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}
