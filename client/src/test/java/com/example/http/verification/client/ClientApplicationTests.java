package com.example.http.verification.client;

import java.io.File;

import com.example.http.verification.client.clients.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;
import org.springframework.experimental.boot.server.exec.FileClasspathEntry;
import org.springframework.experimental.boot.server.exec.MavenClasspathEntry;
import org.springframework.experimental.boot.test.context.DynamicProperty;
import org.springframework.experimental.boot.test.context.EnableDynamicProperty;
import org.springframework.experimental.boot.test.context.OAuth2ClientProviderIssuerUri;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIf("isServerJarPresent")
class ClientApplicationTests {

	private static String SERVER_JAR = "../server/target/server-0.0.1-SNAPSHOT.jar";

	public static void main(String[] args) {
		SpringApplication.from(ClientApplication::main).with(ExtraConfiguration.class).run(args);
	}

	static boolean isServerJarPresent() {
		return new File(SERVER_JAR).exists();
	}

	@Test
	void personServiceTest(@Autowired PersonService people) {
		assertThat(people.test()).isEqualTo("test");
	}

	@TestConfiguration(proxyBeanMethods = false)
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

		@Bean
		@DependsOn("authServer")
		@DynamicProperty(name = "remote.server.port", value = "port")
		static CommonsExecWebServerFactoryBean server(Environment environment) throws Exception {
			// This won't be set in time, so we need to fix all the ports
			System.err.println("************* " + environment.getProperty("spring.security.oauth2.client.provider.spring.issuer-uri"));
			return CommonsExecWebServerFactoryBean.builder()
					.useRandomPort(false)
					.classpath(classpath -> classpath
							.entries(new FileClasspathEntry(SERVER_JAR)));
		}

	}

}
