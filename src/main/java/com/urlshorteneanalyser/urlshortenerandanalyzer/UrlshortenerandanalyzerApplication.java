package com.urlshorteneanalyser.urlshortenerandanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot Application Class
 * 
 * This is the entry point for the URL Shortener and Analyzer application.
 * It configures Spring Boot to scan for components across multiple packages
 * and sets up JPA repositories and entity scanning.
 * 
 * Key configurations:
 * - @SpringBootApplication: Enables auto-configuration and component scanning
 * - @EnableJpaRepositories: Configures Spring Data JPA repositories
 * - @EntityScan: Tells Spring where to find JPA entities
 */
@SpringBootApplication(scanBasePackages = {"com.urlshorteneanalyser.urlshortenerandanalyzer", "Controller", "Service", "Repository", "Model"})
@EnableJpaRepositories(basePackages = {"Repository"})
@EntityScan(basePackages = {"Model"})
public class UrlshortenerandanalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlshortenerandanalyzerApplication.class, args);
	}

}
