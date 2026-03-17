package com.ofp.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Bean
	NewTopic paymentAuthorizedTopic() {
		return TopicBuilder.name("payment.authorized")
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	NewTopic paymentFailedTopic() {
		return TopicBuilder.name("payment.failed")
				.partitions(3)
				.replicas(1)
				.build();
	}
	
}
