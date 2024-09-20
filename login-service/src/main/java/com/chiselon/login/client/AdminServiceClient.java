package com.chiselon.login.client;

import com.chiselon.login.model.Admin;
import com.chiselon.login.model.Employee;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Employee", url = "http://localhost:8002")
public interface AdminServiceClient {

	@GetMapping("/employee/validate")
    Employee validateEmployeeCredentials(@RequestParam("emailId") String emailId,
                                        @RequestParam("password") String password);
}
