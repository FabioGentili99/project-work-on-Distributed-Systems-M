package it.unibo.SCF_example.SCF_dependency_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.util.function.Function;


@SpringBootApplication
public class ScfDependencyInjectionApplication {

	@Autowired
	private ResponseBuilder rb;

	public static void main(String[] args) {
		SpringApplication.run(ScfDependencyInjectionApplication.class, args);
	}

	@Bean
	public Function<String, String> greetings(){
		return value -> rb.getResponse(value);
	}
}
