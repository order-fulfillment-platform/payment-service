package com.ofp.paymentservice.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.paymentservice.dto.StockReservedEvent;
import com.ofp.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockReservedConsumer {

	private final PaymentService paymentService;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "stock.reserved", groupId = "payment-service")
	public void consume(String message) {
		log.info("Received STOCK_RESERVED event: {}", message);
		try {
			StockReservedEvent event = objectMapper.readValue(message, StockReservedEvent.class);
			paymentService.processStockReserved(event);
		} catch (Exception e) {
			log.error("Failed to process STOCK_RESERVED event: {}", message, e);
		}
	}
}
