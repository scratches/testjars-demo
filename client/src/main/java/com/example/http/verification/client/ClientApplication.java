package com.example.http.verification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.http.verification.client.clients.PersonService;
import com.example.http.verification.client.clients.VerificationService;

@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Bean
	@Lazy
	public PersonService personService(RestClient.Builder builder, ClientRegistrationRepository repository,
			OAuth2AuthorizedClientService service,
			@Value("${remote.server.url:http://localhost:8080}") String url) {
		AuthorizedClientServiceOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
				repository, service);
		OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(manager);
		interceptor.setClientRegistrationIdResolver(request -> "spring");
		builder.baseUrl(url).requestInterceptor(interceptor);
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(builder.build()))
				.build();
		return factory.createClient(PersonService.class);
	}

	@Bean
	@Lazy
	public VerificationService verificationService(RestClient.Builder builder, ClientRegistrationRepository repository,
			OAuth2AuthorizedClientService service, @Value("${remote.server.url:http://localhost:8080}") String url) {
		AuthorizedClientServiceOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
				repository, service);
		OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(manager);
		interceptor.setClientRegistrationIdResolver(request -> "spring");
		builder.baseUrl(url).requestInterceptor(interceptor);
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(builder.build()))
				.build();
		return factory.createClient(VerificationService.class);
	}

	@Bean
	public CommandLineRunner runner(PersonService service) {
		return args -> {
			Thread.sleep(2000L);
			System.err.println(service.test());
		};
	}

}
