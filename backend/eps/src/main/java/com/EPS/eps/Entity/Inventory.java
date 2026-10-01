package com.EPS.eps.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity 
@Table(name="inventory")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class Inventory {
    @Id 
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    @OneToOne 
    @JoinColumn(name="product_id",nullable=false,unique=true)
    private Product product;
    @Column(nullable=false)
    private Integer quantity;
}
