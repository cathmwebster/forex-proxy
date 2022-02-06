package com.webster.forexproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ForexProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForexProxyApplication.class, args);
	}

}
