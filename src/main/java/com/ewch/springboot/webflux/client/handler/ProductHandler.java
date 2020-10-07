package com.ewch.springboot.webflux.client.handler;

import com.ewch.springboot.webflux.client.model.Product;
import com.ewch.springboot.webflux.client.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ProductHandler {

	private final ProductService productService;

	public ProductHandler(ProductService productService) {
		this.productService = productService;
	}

	public Mono<ServerResponse> findAllProducts(ServerRequest serverRequest) {
		return ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(productService.findAllProducts(), Product.class);
	}

	public Mono<ServerResponse> findProductById(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return productService.findProductById(id)
			.flatMap(product -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.syncBody(product))
			.switchIfEmpty(ServerResponse.notFound().build());
		/*return ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(productService.findAllProducts(), Product.class);*/
	}


}
