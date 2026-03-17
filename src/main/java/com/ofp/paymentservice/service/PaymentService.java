package com.ofp.paymentservice.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.paymentservice.dto.StockReservedEvent;
import com.ofp.paymentservice.entity.OutboxEvent;
import com.ofp.paymentservice.entity.Payment;
import com.ofp.paymentservice.entity.PaymentStatus;
import com.ofp.paymentservice.repository.OutboxEventRepository;
import com.ofp.paymentservice.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public void processStockReserved(StockReservedEvent event) {
		log.info("Processing STOCK_RESERVED for orderId {}", event.getOrderId());

		// Idempotency check - avoid double payment
		if (paymentRepository.existsByOrderId(event.getOrderId())) {
			log.warn("Payment already exists for orderId {}, skipping", event.getOrderId());
			return;
		}

		// Mock payment authorization - always authorize for MVP
		boolean authorized = mockAuthorizePayment(event);

		// Save payment record
		Payment payment = Payment.builder()
				.orderId(event.getOrderId())
				.customerId(event.getCustomerId())
				.amount(event.getTotalAmount())
				.status(authorized ? PaymentStatus.AUTHORIZED : PaymentStatus.FAILED)
				.build();

		paymentRepository.save(payment);

		// Create outbox event
		String eventType = authorized ? "PAYMENT_AUTHORIZED" : "PAYMENT_FAILED";
		createOutboxEvent(event.getOrderId(), eventType);

		log.info("Payment {} for orderId {}", eventType, event.getOrderId());
	}

	private boolean mockAuthorizePayment(StockReservedEvent event) {
		// Mock logic: authorize if amount < 1000, reject otherwise
		return event.getTotalAmount().doubleValue() < 1000.0;
	}

	private void createOutboxEvent(UUID orderId, String eventType) {
		try {
			String payload = objectMapper.writeValueAsString(Map.of("orderId", orderId.toString()));

			OutboxEvent event = OutboxEvent.builder()
					.aggregateId(orderId)
					.eventType(eventType)
					.payload(payload)
					.build();

			outboxEventRepository.save(event);
		} catch (Exception e) {
			log.error("Failed to create outbox event {}", eventType, e);
			throw new RuntimeException("Failed to create outbox event", e);
		}

	}

}
