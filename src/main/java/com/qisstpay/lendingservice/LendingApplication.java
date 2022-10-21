package com.qisstpay.lendingservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@OpenAPIDefinition
@EnableEurekaClient
@SpringBootApplication
public class LendingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LendingApplication.class, args);
	}

}
