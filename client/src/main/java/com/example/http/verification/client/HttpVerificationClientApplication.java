package com.example.http.verification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.http.verification.client.clients.PersonService;
import com.example.http.verification.client.clients.VerificationService;

@SpringBootApplication
public class HttpVerificationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpVerificationClientApplication.class, args);
	}

	@Bean
	@Lazy
	public PersonService personService(RestClient.Builder builder, @Value("${remote.server.url:http://localhost:8080}") String url) {
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(builder.baseUrl(url).build())).build();
		return factory.createClient(PersonService.class);
	}

	@Bean
	@Lazy
	public VerificationService verificationService(RestClient.Builder builder, @Value("${remote.server.url:http://localhost:8080}") String url) {
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(builder.baseUrl(url).build())).build();
		return factory.createClient(VerificationService.class);
	}

	@Bean
	public CommandLineRunner runner(PersonService service) {
		return args -> {
			System.err.println(service.test());
		};
	}

}
