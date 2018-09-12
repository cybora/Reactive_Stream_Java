package com.bora.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Image {
	
	@Id
	private final String id;
	private final String name;	

}
