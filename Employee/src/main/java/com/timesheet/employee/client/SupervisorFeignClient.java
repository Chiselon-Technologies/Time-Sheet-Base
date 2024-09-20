package com.timesheet.employee.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.timesheet.employee.entity.Supervisor;

@FeignClient(name = "Supervisor" ,url = "http://localhost:8086/supervisors")
public interface SupervisorFeignClient {
	 @PostMapping("/supervisors")
	    Supervisor saveSupervisor(@RequestBody Supervisor supervisor);
}
