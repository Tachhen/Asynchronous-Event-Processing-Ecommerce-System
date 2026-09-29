package com.EPS.eps.Kafka.Inventory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.EPS.eps.DTO.Event.OrderCreatedEvent;

@Service
public class InventoryConsumer {
    @KafkaListener(
        topics="orders",
        groupId="inventory-group"
    )
    public void processInventory(OrderCreatedEvent event){
        System.out.println(
            "Processing inventory for order: "
            + event.getOrderId()
        );
    }
}
