package com.bora.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.bora.model.Image;

import reactor.core.publisher.Mono;

public interface ImageRepository extends ReactiveCrudRepository<Image, String>{
	
	Mono<Image> findByName(String name);

}
