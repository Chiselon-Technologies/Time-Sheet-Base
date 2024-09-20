package com.leave.request.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.leave.request.entity.EmployeeResponse;
import com.leave.request.entity.SupervisorResponse;



@FeignClient(name = "Admin", url = "http://localhost:8081/admin")
public interface AdminProjectClient {

	@GetMapping("projects/employees/{employeeId}")
	EmployeeResponse getEmployeeById(@PathVariable String employeeId);
	
	@GetMapping("projects/supervisors/{supervisorId}")
    SupervisorResponse getSupervisorById(@PathVariable String supervisorId);
	
	
	@GetMapping("projects/supervisors")
	List<SupervisorResponse> getAllSupervisors();
	
}


