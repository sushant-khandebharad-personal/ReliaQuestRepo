package com.example.rqchallenge;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.rqchallenge.employees.EmployeeService;

@Configuration
public class RestTemplateConfiguration {
	
	@Bean
	public  RestTemplate createRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate restTemplate = restTemplateBuilder.build();
		return restTemplate;
	}
	
	
}