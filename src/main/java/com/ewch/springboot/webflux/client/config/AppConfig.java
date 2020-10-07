package com.ewch.springboot.webflux.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Value("${config.base.endpoint}")
	private String urlEndpoint;

	@Bean
	public WebClient registerWebClient() {
		return WebClient.create(urlEndpoint);
	}
}