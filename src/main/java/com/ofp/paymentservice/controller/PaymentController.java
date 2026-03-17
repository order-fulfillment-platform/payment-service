package com.ofp.paymentservice.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofp.paymentservice.dto.PaymentResponse;
import com.ofp.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

	private final PaymentRepository paymentRepository;

	@GetMapping("/order/{orderId}")
	public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable UUID orderId) {
		log.info("Received get payment request for orderId {}", orderId);

		return paymentRepository.findByOrderId(orderId)
				.map(p -> ResponseEntity.ok(PaymentResponse.builder()
						.id(p.getId())
						.orderId(p.getOrderId())
						.customerId(p.getCustomerId())
						.amount(p.getAmount())
						.status(p.getStatus())
						.createdAt(p.getCreatedAt())
						.build()))
				.orElse(ResponseEntity.notFound().build());
	}

}
