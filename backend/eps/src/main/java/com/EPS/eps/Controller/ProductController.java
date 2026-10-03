package com.EPS.eps.Controller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import com.EPS.eps.DTO.ProductDTO;
import com.EPS.eps.Service.ProductService;
import org.springframework.web.bind.annotation.*;

import com.EPS.eps.DTO.ProductDTO;
import com.EPS.eps.Service.ProductService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://asynchronous-event-processing-ecomm.vercel.app"
})
@RestController 
@RequiredArgsConstructor 
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO>createProduct(
        @RequestBody ProductDTO productDTO){
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(productService.createProduct(productDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO>getProductById(
        @PathVariable Long id){
            return ResponseEntity.ok(productService.getProductById(id));
    }

}
