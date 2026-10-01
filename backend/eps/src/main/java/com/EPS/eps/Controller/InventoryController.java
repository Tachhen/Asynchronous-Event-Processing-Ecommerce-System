package com.EPS.eps.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EPS.eps.Entity.Inventory;
import com.EPS.eps.Repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository inventoryRepository;
    
    @GetMapping("/{productId}")
    public ResponseEntity<Inventory>getInventory(@PathVariable Long productId){
        Inventory inventory=inventoryRepository.findByProductId(productId)
                                                .orElseThrow(()->new RuntimeException("Inventory not found"));
        return ResponseEntity.ok(inventory);
    }
}
