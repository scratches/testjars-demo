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
import org.springframework.experimental.boot.test.context.DynamicPortUrl;
import org.springframework.experimental.boot.test.context.EnableDynamicProperty;

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
	void contextLoads() {
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableDynamicProperty
	static class ExtraConfiguration {

		@Bean
		@DynamicPortUrl(name = "remote.server.url")
		static CommonsExecWebServerFactoryBean server() throws Exception {
			return CommonsExecWebServerFactoryBean.builder()
					.useRandomPort(false)
					.classpath(classpath -> classpath
							.entries(new FileClasspathEntry(SERVER_JAR)));
		}

	}

}
