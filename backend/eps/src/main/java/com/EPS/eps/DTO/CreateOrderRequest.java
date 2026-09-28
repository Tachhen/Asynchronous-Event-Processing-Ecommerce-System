package com.EPS.eps.DTO;
import java.util.*;
import lombok.*;

//make me this product basically contaisn what orderItemswe want in the form of a List of OrderItemRequest
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class CreateOrderRequest {
    private List<OrderItemRequest>items;
}
