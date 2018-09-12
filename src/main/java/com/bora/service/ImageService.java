package com.bora.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import com.bora.model.Image;
import com.bora.repository.ImageRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ImageService {
	
	private static String UPLOAD_ROOT = "C:\\Users\\02483682\\Desktop\\BULGULAR\\myApp";
	
	private final ResourceLoader resourceLoader;
	
	private final ImageRepository imageRepository;
	
	public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository) {
		this.resourceLoader = resourceLoader;
		this.imageRepository = imageRepository;
	}
	
	public Flux<Image> findAllImages() {
		return imageRepository.findAll();
	}
	
	public Mono<Resource> findOneImage(String fileName) {
		return Mono.fromSupplier(() -> 
			resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + fileName));
	}
	
	public Mono<Void> createImage(Flux<FilePart> files) {
		return files.flatMap(file -> {
			Mono<Image> saveDatabaseImage = 
					imageRepository.save(new Image(UUID.randomUUID().toString(), file.filename()));
			
			Mono<Void> copyFile = Mono.just(
					Paths.get(UPLOAD_ROOT, file.filename())
					.toFile())
					.log("createImage-picktarget")
					.map(destFile -> {
						try {
							destFile.createNewFile();
							return destFile;
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}).log("createImage-newFile")
					.flatMap(file::transferTo)
					.log("createImage-copy");
			
			return Mono.when(saveDatabaseImage, copyFile);
					
		}).then();
	}
	
	public Mono<Void> deleteImage(String fileName) {
		
		Mono<Void> deleteDatabaseImage = imageRepository
				.findByName(fileName)
				.flatMap(imageRepository::delete);
		Mono<Void> deleteFile = Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		
		return Mono.when(deleteDatabaseImage, deleteFile).then();
	}
	
	@Bean
	CommandLineRunner setUp() throws IOException {
		return args -> {
			FileSystemUtils.deleteRecursively(Paths.get(UPLOAD_ROOT));
			
			Files.createDirectories(Paths.get(UPLOAD_ROOT));
			
			FileCopyUtils.copy("test file1", new FileWriter(UPLOAD_ROOT + "/file1.jpg"));
			FileCopyUtils.copy("test file2", new FileWriter(UPLOAD_ROOT + "/file2.jpg"));
			FileCopyUtils.copy("test file3", new FileWriter(UPLOAD_ROOT + "/file3.jpg"));		
		};
	}
	

}
