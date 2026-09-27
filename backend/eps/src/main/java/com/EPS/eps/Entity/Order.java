package com.EPS.eps.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="orders")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Order {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable=false)
    private BigDecimal totalAmount;
    @Enumerated (EnumType.STRING)
    private OrderStatus status;
    @Column(nullable=false)
    private LocalDateTime createdAt;
    @OneToMany(
        mappedBy="order",
        cascade=CascadeType.ALL
    )
    private List<OrderItem>items=new ArrayList<>();
}
