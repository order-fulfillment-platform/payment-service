package com.ofp.paymentservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.ofp.paymentservice.entity.Payment;
import com.ofp.paymentservice.entity.PaymentStatus;
import com.ofp.paymentservice.repository.OutboxEventRepository;
import com.ofp.paymentservice.repository.PaymentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PaymentControllerIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

	@Container
	static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@BeforeEach
	void setUp() {
		outboxEventRepository.deleteAll();
		paymentRepository.deleteAll();
	}

	@Test
	void getPaymentsByOrderId_shouldReturn404_whenPaymentNotFound() throws Exception {
		mockMvc.perform(get("/api/v1/payments/order/" + UUID.randomUUID()))
				.andExpect(status().isNotFound());
	}

	@Test
	void getPaymentByOrderId_shouldReturn200_withFailedStatus() throws Exception {
		// Arrange
		UUID orderId = UUID.randomUUID();
		paymentRepository.save(Payment.builder()
				.orderId(orderId)
				.customerId(UUID.randomUUID())
				.amount(new BigDecimal("1500.00"))
				.status(PaymentStatus.FAILED)
				.build());

		// Act & Assert
		mockMvc.perform(get("/api/v1/payments/order/" + orderId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderId").value(orderId.toString()))
				.andExpect(jsonPath("$.status").value("FAILED"))
				.andExpect(jsonPath("$.amount").value(1500.00));

		assertThat(paymentRepository.findAll()).hasSize(1);
	}


}
