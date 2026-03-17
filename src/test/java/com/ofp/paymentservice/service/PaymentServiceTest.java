package com.ofp.paymentservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofp.paymentservice.dto.StockReservedEvent;
import com.ofp.paymentservice.entity.OutboxEvent;
import com.ofp.paymentservice.entity.Payment;
import com.ofp.paymentservice.entity.PaymentStatus;
import com.ofp.paymentservice.repository.OutboxEventRepository;
import com.ofp.paymentservice.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private PaymentService paymentService;

	@Test
	void processStockReserved_shouldAuthorizePayment_whenAmountBelow1000() throws Exception {
		// Arrange
		StockReservedEvent event = new StockReservedEvent();
		event.setOrderId(UUID.randomUUID());
		event.setCustomerId(UUID.randomUUID());
		event.setTotalAmount(new BigDecimal("109.97"));

		when(paymentRepository.existsByOrderId(any())).thenReturn(false);
		when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(outboxEventRepository.save(any())).thenReturn(null);

		// Act
		paymentService.processStockReserved(event);

		// Assert
		verify(paymentRepository, times(1)).save(argThat(payment ->
				((Payment) payment).getStatus() == PaymentStatus.AUTHORIZED));
		verify(outboxEventRepository, times(1)).save(argThat(event2 ->
				((OutboxEvent) event2).getEventType().equals("PAYMENT_AUTHORIZED")));
	}

	@Test
	void processStockReserved_shouldFailPayment_whenAmountAbove1000() throws Exception {
		// Arrange
		StockReservedEvent event = new StockReservedEvent();
		event.setOrderId(UUID.randomUUID());
		event.setCustomerId(UUID.randomUUID());
		event.setTotalAmount(new BigDecimal("1500.00"));

		when(paymentRepository.existsByOrderId(any())).thenReturn(false);
		when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		when(outboxEventRepository.save(any())).thenReturn(null);

		// Act
		paymentService.processStockReserved(event);

		// Assert
		verify(paymentRepository, times(1)).save(argThat(payment ->
				((Payment) payment).getStatus() == PaymentStatus.FAILED));
		verify(outboxEventRepository, times(1)).save(argThat(event2 ->
				((OutboxEvent) event2).getEventType().equals("PAYMENT_FAILED")));
	}

	@Test
	void processStockReserved_shouldSkip_whenPaymentAlreadyExists() throws Exception {
		// Arrange
		StockReservedEvent event = new StockReservedEvent();
		event.setOrderId(UUID.randomUUID());
		event.setCustomerId(UUID.randomUUID());
		event.setTotalAmount(new BigDecimal("109.97"));

		when(paymentRepository.existsByOrderId(any())).thenReturn(true);

		// Act
		paymentService.processStockReserved(event);

		// Assert
		verify(paymentRepository, never()).save(any());
		verify(outboxEventRepository, never()).save(any());
	}

}
