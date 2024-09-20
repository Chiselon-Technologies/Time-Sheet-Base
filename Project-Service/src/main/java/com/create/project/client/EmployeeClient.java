package com.create.project.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.create.project.entity.Employee;

@FeignClient(name = "Employee", url = "http://localhost:8002")
public interface EmployeeClient {
	
	 @GetMapping("/employee/{employeeId}")
	    Employee getEmployeeById(@PathVariable("employeeId") String employeeId);
	 
	
	
	    @PutMapping("/employee/{employeeId}")
	    void updateEmployee(@PathVariable("employeeId") String employeeId, @RequestBody Employee employee);

	    @DeleteMapping("/employee/{employeeId}")
	    void deleteEmployee(@PathVariable("employeeId") String employeeId);
	    
	    
	    @DeleteMapping("/employee/emp/{employeeId}")
	    void deleteEmployeeProj(@PathVariable("employeeId") String employeeId);
}
