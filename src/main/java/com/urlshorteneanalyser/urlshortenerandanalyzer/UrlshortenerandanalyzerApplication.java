package com.urlshorteneanalyser.urlshortenerandanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.urlshorteneanalyser.urlshortenerandanalyzer", "Controller", "Service", "Repository", "Model"})
@EnableJpaRepositories(basePackages = {"Repository"})
@EntityScan(basePackages = {"Model"})
public class UrlshortenerandanalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlshortenerandanalyzerApplication.class, args);
	}

}
