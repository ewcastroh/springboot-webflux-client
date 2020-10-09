package com.ewch.springboot.webflux.client.service;

import com.ewch.springboot.webflux.client.model.Product;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

	private final WebClient webClient;

	public ProductServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public Flux<Product> findAllProducts() {
		return webClient.get()
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.exchange()
			.flatMapMany(clientResponse -> clientResponse.bodyToFlux(Product.class));
	}

	@Override
	public Mono<Product> findProductById(String id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		return webClient.get()
			.uri("/{id}", params)
			.accept(MediaType.APPLICATION_JSON_UTF8)
			// .exchange()
			// .flatMap(clientResponse -> clientResponse.bodyToMono(Product.class));
			.retrieve()
			.bodyToMono(Product.class);
	}

	@Override
	public Mono<Product> saveProduct(Product product) {
		return webClient.post()
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			// .body(BodyInserters.fromObject(product))
			.syncBody(product)
			.retrieve()
			.bodyToMono(Product.class);
	}

	@Override
	public Mono<Product> updateProduct(Product product, String id) {
		return webClient.put()
			.uri("/{id}", Collections.singletonMap("id", id))
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			// .body(BodyInserters.fromObject(product))
			.syncBody(product)
			.retrieve()
			.bodyToMono(Product.class);
	}

	@Override
	public Mono<Void> deleteProduct(String id) {
		return webClient.delete()
			.uri("/{id}", Collections.singletonMap("id", id))
			// .exchange()
			// .then();
			.retrieve()
			.bodyToMono(Void.class);
	}

	@Override
	public Mono<Product> uploadProductPicture(FilePart filePart, String id) {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.asyncPart("file", filePart.content(), DataBuffer.class)
			.headers(httpHeaders -> {
				httpHeaders.setContentDispositionFormData("file", filePart.filename());
			});
		return webClient.post()
			.uri("/uploads/picture/", Collections.singletonMap("id", id))
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.syncBody(multipartBodyBuilder.build())
			.retrieve()
			.bodyToMono(Product.class);
	}
}
