package com.microservice.profileservice;

import com.microservice.profileservice.service.ProfileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@ComponentScan({"com.microservice.profileservice", "com.microservice.commonservice"})
public class ProfileserviceApplication {
	private ProfileService profileService;
	public static void main(String[] args) {
		SpringApplication.run(ProfileserviceApplication.class, args);
	}

}
