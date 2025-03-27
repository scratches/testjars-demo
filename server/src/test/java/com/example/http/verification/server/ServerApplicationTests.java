package com.example.http.verification.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.example.http.verification.ServerApplication;
import com.example.http.verification.controllers.PersonController;

@WebMvcTest(PersonController.class)
@TestPropertySource(locations = "classpath:test.properties")
class ServerApplicationTests {

	@Autowired
	private MockMvcTester mvc;

	public static void main(String[] args) {
		SpringApplication.from(ServerApplication::main).run(args);
	}

	@Test
	void contextLoads() throws Exception {
		assertThat(mvc.get().uri("/persons/test").with(SecurityMockMvcRequestPostProcessors.jwt()).exchange()
				.getResponse().getContentAsString()).isEqualTo("test");
	}

}
