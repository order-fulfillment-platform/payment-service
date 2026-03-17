package com.ofp.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ofp.paymentservice.entity.PaymentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
	private UUID id;
	private UUID orderId;
	private UUID customerId;
	private BigDecimal amount;
	private PaymentStatus status;
	private LocalDateTime createdAt;
}
