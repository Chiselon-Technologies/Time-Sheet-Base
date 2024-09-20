package com.create.project.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.create.project.entity.Supervisor;

@FeignClient(name = "Supervisor" ,url = "http://localhost:8086")
public interface SupervisorClient {
	 
	 @PostMapping("/supervisors") 
	    Supervisor createSupervisor(@RequestBody Supervisor supervisor);
	 
	 
	 @GetMapping("/supervisors/{supervisorId}")
	    Supervisor getSupervisorById(@PathVariable("supervisorId") String supervisorId);

}
