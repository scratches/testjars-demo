package com.example.http.verification.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

import com.example.http.verification.client.clients.PersonService;

@SpringBootApplication
@ImportHttpServices(basePackageClasses = PersonService.class)
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Bean
	public RestClientHttpServiceGroupConfigurer groupConfigurer(Environment environment) {
		return groups -> groups
				.configureClient((group, builder) -> builder
						.baseUrl(environment.getProperty("remote.server.url", "http://localhost:8080")));
	}

	@Bean
	public CommandLineRunner runner(PersonService service) {
		return args -> {
			System.err.println(service.test());
		};
	}

}
