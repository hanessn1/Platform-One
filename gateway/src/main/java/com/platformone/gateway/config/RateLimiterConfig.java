package com.platformone.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.security.Principal;

@Configuration
public class RateLimiterConfig {
    @Bean
    @Primary
    KeyResolver userOrIpKeyResolver() {
        return exchange -> {
            return exchange.getPrincipal()
                    .map(Principal::getName)
                    .defaultIfEmpty(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }
}