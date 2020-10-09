package com.ewch.springboot.webflux.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Value("${config.base.endpoint}")
	private String urlEndpoint;

	@Bean
	// To work as a microservice
	@LoadBalanced
	public WebClient.Builder registerWebClient() {
		// return WebClient.create(urlEndpoint);
		// To work as a microservice
		return WebClient.builder().baseUrl(urlEndpoint);
	}
}