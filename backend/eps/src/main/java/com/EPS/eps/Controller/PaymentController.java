package com.EPS.eps.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EPS.eps.DTO.Event.PaymentCompleteEvent;
import com.EPS.eps.Kafka.Payment.PaymentCompleteProducer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCompleteProducer paymentCompleteProducer;

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<String> completePayment(
            @PathVariable Long orderId) {

        PaymentCompleteEvent event =
                new PaymentCompleteEvent(orderId);

        paymentCompleteProducer.sendPaymentComplete(event);

        return ResponseEntity.ok("Payment event sent");
    }

    @PostMapping("/{orderId}/duplicate")
    public ResponseEntity<String> duplicatePayment(
            @PathVariable Long orderId) {

        PaymentCompleteEvent event =
                new PaymentCompleteEvent(orderId);

        paymentCompleteProducer.sendDuplicatePayment(event);

        return ResponseEntity.ok("Duplicate payment events sent");
    }
}