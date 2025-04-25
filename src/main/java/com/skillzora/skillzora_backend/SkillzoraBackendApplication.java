package com.skillzora.skillzora_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class SkillzoraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillzoraBackendApplication.class, args);
    }
}
