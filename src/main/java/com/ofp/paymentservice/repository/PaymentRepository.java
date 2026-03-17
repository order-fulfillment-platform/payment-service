package com.ofp.paymentservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofp.paymentservice.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	boolean existsByOrderId(UUID orderId);

	Optional<Payment> findByOrderId(UUID orderId);
}
