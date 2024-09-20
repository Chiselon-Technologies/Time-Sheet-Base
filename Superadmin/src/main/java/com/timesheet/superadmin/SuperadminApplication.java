package com.timesheet.superadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SuperadminApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperadminApplication.class, args);
	}

}
