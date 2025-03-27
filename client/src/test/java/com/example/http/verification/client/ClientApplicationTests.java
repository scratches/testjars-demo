package com.example.http.verification.client;

import java.io.File;

import com.example.http.verification.client.clients.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServer;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;
import org.springframework.experimental.boot.server.exec.FileClasspathEntry;
import org.springframework.experimental.boot.server.exec.MavenClasspathEntry;
import org.springframework.experimental.boot.test.context.DynamicPortUrl;
import org.springframework.experimental.boot.test.context.OAuth2ClientProviderIssuerUri;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIf("isServerJarPresent")
class ClientApplicationTests {

	private static String SERVER_JAR = "../server/target/server-0.0.1-SNAPSHOT.jar";

	static boolean isServerJarPresent() {
		return new File(SERVER_JAR).exists();
	}

	@Test
	void personServiceTest(@Autowired PersonService people) {
		assertThat(people.test()).isEqualTo("test");
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class ExtraConfiguration {

		@Bean
		@OAuth2ClientProviderIssuerUri
		static CommonsExecWebServerFactoryBean authServer() {
			return CommonsExecWebServerFactoryBean.builder()
					.defaultSpringBootApplicationMain()
					.classpath(classpath -> classpath
							.entries(MavenClasspathEntry.springBootStarter("oauth2-authorization-server")));
		}

		@Bean
		@DynamicPortUrl(name = "remote.server.url")
		static CommonsExecWebServerFactoryBean server(CommonsExecWebServer authServer) throws Exception {
			String issuerUriProp = "spring.security.oauth2.client.provider.spring.issuer-uri";
			String issuerUri = "http://127.0.0.1:" + authServer.getPort();
			return CommonsExecWebServerFactoryBean.builder()
					.systemProperties(s -> s.put(issuerUriProp, issuerUri))
					.classpath(classpath -> classpath
							.entries(new FileClasspathEntry(SERVER_JAR)));
		}

	}

}
