package com.example.OrangeToolzProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class OrangeToolzProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrangeToolzProjectApplication.class, args);
	}

}
