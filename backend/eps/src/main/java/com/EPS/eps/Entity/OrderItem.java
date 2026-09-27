package com.EPS.eps.Entity;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity 
@Table(name="order_items")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
