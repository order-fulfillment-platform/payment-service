package com.ofp.paymentservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", nullable = false,  updatable = false)
	private UUID id;

	@Column(name = "aggregate_id", nullable = false)
	private UUID aggregateId;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column (name = "payload", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(name = "created_at", nullable = false,  updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "processed", nullable = false)
	private boolean processed;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.processed = false;
	}


}
