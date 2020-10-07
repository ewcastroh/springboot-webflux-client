package com.ewch.springboot.webflux.client.model;

import java.util.Date;

public class Product {

	private String id;
	private String name;
	private Double price;
	private Date createdAt;
	private Category category;
	private String picture;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return "Product{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			", price=" + price +
			", createdAt=" + createdAt +
			", category=" + category +
			", picture='" + picture + '\'' +
			'}';
	}
}
