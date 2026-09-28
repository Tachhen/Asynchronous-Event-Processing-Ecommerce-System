package com.EPS.eps.DTO;

import java.math.BigDecimal;

import lombok.*;

//U wanted an orderItem here is the details of the product u wanted with quantity and stuff
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
