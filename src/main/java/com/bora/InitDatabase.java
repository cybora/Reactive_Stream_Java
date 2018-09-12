package com.bora;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.bora.model.Image;

@Component
public class InitDatabase {
	
	@Bean
	CommandLineRunner init(MongoOperations mongoOperations) {
		return args -> {
			mongoOperations.dropCollection(Image.class);
			
			mongoOperations.insert(new Image("1", "bora1"));
			mongoOperations.insert(new Image("2", "bora2"));
			mongoOperations.insert(new Image("3", "bora3"));
			
			mongoOperations.findAll(Image.class)
				.forEach(image -> System.out.println(image.toString()));
		};
	}

}
