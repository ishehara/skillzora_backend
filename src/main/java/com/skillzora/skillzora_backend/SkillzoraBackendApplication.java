package com.skillzora.skillzora_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableMongoRepositories
public class SkillzoraBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillzoraBackendApplication.class, args);
	}

	@GetMapping("/")
	public String rootEndString(){
		String message = "Hello world!";
		return message;
	}

}
