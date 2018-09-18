package com.bora.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import com.bora.model.Image;
import com.bora.service.ImageService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTest {
	
	@Autowired
	WebTestClient webClient;
	
	@MockBean
	ImageService imageService;
	
	@Test
	public void baseRouteShouldListAllImages() {
		Image image1 = new Image("id1", "image1");
		Image image2 = new Image("id2", "image2");
		
		given(imageService.findAllImages())
			.willReturn(Flux.just(image1, image2));
		
		//when
		
		EntityExchangeResult<String> result =  webClient.get()
		.uri("/")
		.exchange()
		.expectStatus().isOk()
		.expectBody(String.class).returnResult();
		
		verify(imageService).findAllImages();
		verifyNoMoreInteractions(imageService);
		assertThat(result.getResponseBody())
			.contains("image1")
			.contains("image2");
	}
	
	@Test
	public void fetchingImageShouldWork() {
		given(imageService.findOneImage(any()))
			.willReturn(Mono.just(new ByteArrayResource("data".getBytes())));
		
		webClient.get()
			.uri("/images/image1/raw")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String.class).isEqualTo("data");
		
		verify(imageService).findOneImage(any());
		verifyNoMoreInteractions(imageService);
	}
	
	@Test
	public void fetchingNullImageShouldFail() throws IOException {
		Resource resource = mock(Resource.class);
		
		given(resource.getInputStream())
			.willThrow(new IOException("Bad file"));
		
		given(imageService.findOneImage(any()))
			.willReturn(Mono.just(resource));
		
		webClient.get()
			.uri("/images/image1/raw")
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(String.class)
			.isEqualTo("Couldn't find image1=> Bad file");
		
		verify(imageService).findOneImage("image1");
		verifyNoMoreInteractions(imageService);
	}
	
	@Test
	public void deleteImageShouldWork() {
		given(imageService.deleteImage(any()))
			.willReturn(Mono.empty());
		
		webClient.delete()
		.uri("/images/image1")
		.exchange()
		.expectStatus().isSeeOther()
		.expectHeader().valueEquals(HttpHeaders.LOCATION, "/");
		
		verify(imageService).deleteImage("image1");
		verifyNoMoreInteractions(imageService);
	}

}
