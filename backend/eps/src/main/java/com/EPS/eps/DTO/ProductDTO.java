package com.EPS.eps.DTO;

import java.math.BigDecimal;

import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//When Adding a new Product to products table
@Setter 
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
}
