package com.example.http.verification.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.config.Customizer.withDefaults;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;
import org.springframework.experimental.boot.server.exec.MavenClasspathEntry;
import org.springframework.experimental.boot.test.context.EnableDynamicProperty;
import org.springframework.experimental.boot.test.context.OAuth2ClientProviderIssuerUri;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.http.verification.ServerApplication;
import com.example.http.verification.service.PersonService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
class ServerApplicationTests {

	@Autowired
	@Qualifier("personService")
	private PersonService service;

	public static void main(String[] args) {
		SpringApplication.from(ServerApplication::main).with(ExtraConfiguration.class).run(args);
	}

	@Test
	void contextLoads() {
		assertThat(this.service.test()).isEqualTo("test");
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties(OAuth2ClientProperties.class)
	@EnableDynamicProperty
	static class ExtraConfiguration {

		@Bean
		@OAuth2ClientProviderIssuerUri
		static CommonsExecWebServerFactoryBean authServer() {
			return CommonsExecWebServerFactoryBean.builder()
					.defaultSpringBootApplicationMain()
					.useRandomPort(false)
					.classpath(classpath -> classpath
							.entries(MavenClasspathEntry.springBootStarter("oauth2-authorization-server")));
		}

		// This would be autoconfigured by Spring Boot if we weren't using an OAuth2
		// client for the test
		@Bean
		SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
			http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
			http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(withDefaults()));
			return http.build();
		}

		@Bean
		@Lazy
		public PersonService personService(RestClient.Builder builder, ClientRegistrationRepository repository,
				OAuth2AuthorizedClientService service,
				@Value("http://localhost:${local.server.port:8080}") String url) {
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

	}

}
