package com.EPS.eps.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EPS.eps.Entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
}
