package com.timesheetcreation.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.timesheetcreation.entity.Project;

@FeignClient(name = "Project", url = "http://localhost:8085/projects")
public interface ProjectClient {
	
	
	 @GetMapping("/{projectId}/supervisors/{employeeId}")
	    List<String> getSupervisorsByProjectAndEmployee(
	            @PathVariable("projectId") String projectId,
	            @PathVariable("employeeId") String employeeId);
	
	 
	 @GetMapping("/supervisor/{supervisorId}")
	    List<Project> getProjectsBySupervisorId(@PathVariable("supervisorId") String supervisorId);
	 

}
