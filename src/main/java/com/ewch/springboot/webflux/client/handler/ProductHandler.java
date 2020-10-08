package com.ewch.springboot.webflux.client.handler;

import com.ewch.springboot.webflux.client.model.Product;
import com.ewch.springboot.webflux.client.service.ProductService;
import java.net.URI;
import java.util.Date;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
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

	public Mono<ServerResponse> createProduct(ServerRequest serverRequest) {
		Mono<Product> productMono = serverRequest.bodyToMono(Product.class);
		return productMono.flatMap(product -> {
			if (product.getCreatedAt() == null) {
				product.setCreatedAt(new Date());
			}
			return productService.saveProduct(product);
		}).flatMap(product -> ServerResponse.created(URI.create("/api/client/products/".concat(product.getId())))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(BodyInserters.fromObject(product))
			//.syncBody(productMono)
		);
	}

	public Mono<ServerResponse> updateProduct(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		Mono<Product> productMono = serverRequest.bodyToMono(Product.class);
		return productMono.flatMap(product -> ServerResponse.created(URI.create("/api/client/products/".concat(id)))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(productService.updateProduct(product, id), Product.class)
		);
	}

	public Mono<ServerResponse> deleteProduct(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return productService.deleteProduct(id)
			.then(ServerResponse.noContent().build());
	}
}
