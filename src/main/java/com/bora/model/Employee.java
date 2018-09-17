package com.bora.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="employees")
public class Employee {
	
	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String role;

}
