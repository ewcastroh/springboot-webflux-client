package com.ewch.springboot.webflux.client.handler;

import com.ewch.springboot.webflux.client.model.Product;
import com.ewch.springboot.webflux.client.service.ProductService;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
		/*return ServerResponse.ok()
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.body(productService.findAllProducts(), Product.class);*/
		return errorHandler(productService.findProductById(id)
			.flatMap(product -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.syncBody(product))
			.switchIfEmpty(ServerResponse.notFound().build())
			);
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
		).onErrorResume(throwable -> {
			WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
			if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.BAD_REQUEST) {
				return ServerResponse.badRequest()
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.syncBody(((WebClientResponseException) throwable).getResponseBodyAsString());
			}
			return Mono.error(webClientResponseException);
		});
	}

	public Mono<ServerResponse> updateProduct(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		Mono<Product> productMono = serverRequest.bodyToMono(Product.class);
		return productMono
			.flatMap(product -> productService.updateProduct(product, id))
			.flatMap(product -> ServerResponse.created(URI.create("/api/client/products/".concat(product.getId())))
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.syncBody(product))
			.onErrorResume(throwable -> {
				WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
				if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
					return ServerResponse.notFound().build();
				}
				return Mono.error(webClientResponseException);
			});
	}

	public Mono<ServerResponse> deleteProduct(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return errorHandler(productService.deleteProduct(id)
			.then(ServerResponse.noContent().build())
			.onErrorResume(throwable -> {
				WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
				if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
					return ServerResponse.notFound().build();
				}
				return Mono.error(webClientResponseException);
			})
		);
	}

	public Mono<ServerResponse> uploadProductPicture(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return errorHandler(serverRequest.multipartData()
			.map(stringPartMultiValueMap -> stringPartMultiValueMap.toSingleValueMap().get("file"))
			.cast(FilePart.class)
			.flatMap(filePart -> productService.uploadProductPicture(filePart, id))
			.flatMap(product -> ServerResponse.created(URI.create("/api/client/products/upload/".concat(id)))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.syncBody(product))
		);
	}

	private Mono<ServerResponse> errorHandler(Mono<ServerResponse> serverResponseMono) {
		return serverResponseMono
			.onErrorResume(throwable -> {
				WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
				if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
					Map<String, Object> bodyStringObjectMap = new HashMap<>();
					bodyStringObjectMap.put("error", "Product doesn't exist: ".concat(webClientResponseException.getMessage()));
					bodyStringObjectMap.put("timestamp", new Date());
					bodyStringObjectMap.put("status", webClientResponseException.getStatusCode().value());
					return ServerResponse.status(HttpStatus.NOT_FOUND).syncBody(bodyStringObjectMap);
				}
				return Mono.error(webClientResponseException);
			});
	}
}
