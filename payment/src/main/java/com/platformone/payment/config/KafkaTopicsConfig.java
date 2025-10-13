package com.platformone.payment.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic bookingCreatedTopic() {
        return TopicBuilder.name("booking_created").build();
    }

    @Bean
    public NewTopic paymentSucceededTopic() {
        return TopicBuilder.name("payment_succeeded").build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name("payment_failed").build();
    }
}