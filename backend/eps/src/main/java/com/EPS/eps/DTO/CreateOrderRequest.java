package com.EPS.eps.DTO;
import java.util.*;
import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class CreateOrderRequest {
    private List<OrderItemRequest>items;
}
