package com.bora.endtoend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.openqa.selenium.chrome.ChromeDriverService.createDefaultService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EndToEndTests {
	
	static ChromeDriverService service;
	static ChromeDriver driver;
	
	@LocalServerPort
	int port;
	
	@BeforeClass
	public static void setUp() throws IOException {
		System.setProperty("webdriver.chrome.driver", "ext/chromedriver");
		service = createDefaultService();
		driver = new ChromeDriver(service);
		Path testResults = Paths.get("build", "test-results");
		
		if (!Files.exists(testResults)) {
			Files.createDirectory(testResults);
		}
	}
	
	@AfterClass
	public static void tearDown() {
		service.stop();
	}

}
