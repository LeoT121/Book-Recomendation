package com.Leo.BookRecomendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.Leo.BookRecomendation.config",
    "com.Leo.BookRecomendation.controller",
    "com.Leo.BookRecomendation.entity",
    "com.Leo.BookRecomendation.repository",
    "com.Leo.BookRecomendation.service",
    "com.Leo.BookRecomendation.SistemaAutenticacion"
})
@EnableJpaRepositories(basePackages = "com.Leo.BookRecomendation.repository")
@EntityScan(basePackages = "com.Leo.BookRecomendation.entity")
public class BookRecomendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookRecomendationApplication.class, args);
	}

}
