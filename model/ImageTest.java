package com.bora.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import com.bora.repository.ImageRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class ImageTest {
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Before
	public void setUp() {
		mongoOperations.dropCollection(Image.class);
		
		mongoOperations.insert(new Image("id1", "image1.jpg"));
		mongoOperations.insert(new Image("id2", "image2.jpg"));
		mongoOperations.insert(new Image("id3", "image3.jpg"));
		
		mongoOperations.findAll(Image.class)
			.forEach(image -> System.out.println(image.toString()));
	}
	
	@Test
	public void imageTest() {
	Image image = new Image("id", "file-name.jpg");
	
	assertThat(image.getId()).isEqualTo("id");
	assertThat(image.getName()).isEqualTo("file-name.jpg");
	}
	
	@Test
	public void findAllShouldWork() {
		Flux<Image> images = imageRepository.findAll();
		StepVerifier.create(images)
			.recordWith(ArrayList::new)
			.expectNextCount(3)
			.consumeRecordedWith(results -> {
				assertThat(results).hasSize(3);
				assertThat(results).extracting(Image::getName)
					.contains("image1.jpg", "image2.jpg", "image3.jpg");
			})
			.expectComplete()
			.verify();
	}
	
	@Test
	public void findByNameShouldWork() {
		Mono<Image> image = imageRepository.findByName("image2.jpg");
		
		StepVerifier.create(image)
			.expectNextMatches(result -> {
				assertThat(result.getId()).isEqualTo("id2");
				assertThat(result.getName()).isEqualTo("image2.jpg");
				return true;
			});
	}

}
