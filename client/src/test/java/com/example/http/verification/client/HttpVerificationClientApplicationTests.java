package com.example.http.verification.client;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;
import org.springframework.experimental.boot.server.exec.FileClasspathEntry;

@SpringBootTest // (properties = "client.base-url=http://localhost:${remote.server.port}")
@EnabledIf("isServerJarPresent")
class HttpVerificationClientApplicationTests {

	private static String SERVER_JAR = "../server/target/server-0.0.1-SNAPSHOT.jar";

	public static void main(String[] args) {
		SpringApplication.from(HttpVerificationClientApplication::main).with(ExtraConfiguration.class).run(args);
	}

	static boolean isServerJarPresent() {
		return new File(SERVER_JAR).exists();
	}

	@Test
	void contextLoads() {
	}

	@TestConfiguration(proxyBeanMethods = false)
	// @EnableDynamicProperty
	static class ExtraConfiguration {

		@Bean
		// @DynamicProperty(name = "remote.server.port", value = "port")
		static CommonsExecWebServerFactoryBean verificationServer() throws Exception {
			return CommonsExecWebServerFactoryBean.builder()
					.useRandomPort(false)
					.classpath(classpath -> classpath
							.entries(new FileClasspathEntry(SERVER_JAR)));
		}

	}

}
