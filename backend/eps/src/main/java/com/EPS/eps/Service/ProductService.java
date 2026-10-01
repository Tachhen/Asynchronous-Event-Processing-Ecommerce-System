package com.EPS.eps.Service;

import java.util.*;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.ProductDTO;
import com.EPS.eps.Entity.Inventory;
import com.EPS.eps.Entity.Product;
import com.EPS.eps.Repository.InventoryRepository;
import com.EPS.eps.Repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class ProductService {
    private final ProductRepository productRepository;  
    private final InventoryRepository inventoryRepository;

    @Transactional 
    public ProductDTO createProduct(ProductDTO dto){
        Product product=new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        Product savedProduct=productRepository.save(product);
        Inventory inventory=new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(10);
        inventoryRepository.save(inventory);
        return new ProductDTO(
            savedProduct.getId(),
            savedProduct.getName(),
            savedProduct.getPrice()
        );
    }
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                ))
                .toList();
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice()
        );    
    }
}
