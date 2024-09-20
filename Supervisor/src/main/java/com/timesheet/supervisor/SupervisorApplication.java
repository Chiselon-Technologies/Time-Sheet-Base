package com.timesheet.supervisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SupervisorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupervisorApplication.class, args);
	}

}
