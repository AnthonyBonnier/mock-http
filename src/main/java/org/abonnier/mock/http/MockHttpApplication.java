package org.abonnier.mock.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MockHttpApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockHttpApplication.class, args);
	}
}
