package com.ofp.paymentservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class StockReservedEvent {
	private UUID orderId;
	private UUID customerId;
	private BigDecimal totalAmount;
}
