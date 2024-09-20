package com.chiselon.ssuperadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SsuperAdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsuperAdminServiceApplication.class, args);
	}

}
