package com.example.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SampleAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleAppApplication.class, args);
    }
}
