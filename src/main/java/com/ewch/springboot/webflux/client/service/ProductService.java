package com.ewch.springboot.webflux.client.service;

import com.ewch.springboot.webflux.client.model.Product;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

	Flux<Product> findAllProducts();

	Mono<Product> findProductById(String id);

	Mono<Product> saveProduct(Product product);

	Mono<Product> updateProduct(Product product, String id);

	Mono<Void> deleteProduct(String id);

	Mono<Product> uploadProductPicture(FilePart filePart, String id);
}
