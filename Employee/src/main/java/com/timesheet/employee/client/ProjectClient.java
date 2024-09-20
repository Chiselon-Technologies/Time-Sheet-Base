package com.timesheet.employee.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Admin")
public interface ProjectClient {
	
	  @GetMapping("/projects/{id}")
	    Project getProjectById(@PathVariable("id") String projectId);
	

}

