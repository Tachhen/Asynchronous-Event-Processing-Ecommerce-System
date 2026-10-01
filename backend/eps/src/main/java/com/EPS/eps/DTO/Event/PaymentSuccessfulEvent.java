package com.EPS.eps.DTO.Event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter 
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
public class PaymentSuccessfulEvent {
    private Long orderId;
    private BigDecimal amount;
}
