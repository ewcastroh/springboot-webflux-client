package com.ewch.springboot.webflux.client.config;

import com.ewch.springboot.webflux.client.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
		return RouterFunctions.route(
			RequestPredicates.GET("/api/client/products"), productHandler::findAllProducts)
			.andRoute(RequestPredicates.GET("/api/client/products/{id}"), productHandler::findProductById);
	}
}
