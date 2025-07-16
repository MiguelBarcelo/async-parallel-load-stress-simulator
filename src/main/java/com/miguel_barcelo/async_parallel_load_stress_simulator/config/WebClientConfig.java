package com.miguel_barcelo.async_parallel_load_stress_simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Bean
	WebClient webClient() {
		return WebClient.builder()
				.baseUrl("http://localhost:8080/api/simulator")
				.build();
	}
}
