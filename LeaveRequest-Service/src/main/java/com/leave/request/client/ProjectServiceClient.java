package com.leave.request.client;



import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.leave.request.entity.EmployeeResponse;
import com.leave.request.entity.ProjectResponse;
import com.leave.request.entity.SupervisorResponse;




@FeignClient(name = "Project", url = "http://localhost:8085")
public interface ProjectServiceClient {

    @GetMapping("projects/projects/supervisor-for-employee")
    boolean isSupervisorForEmployee(@RequestParam("supervisorId") String supervisorId, @RequestParam("empId") String empId);
    
    
 
    
    @GetMapping("/projects/employees/{employeeId}")
    EmployeeResponse getEmployeeById(@PathVariable String employeeId);
    
    
    
    @GetMapping("/projects/supervisors/{supervisorId}")
    SupervisorResponse getSupervisorById(@PathVariable String supervisorId);



    
    // Method to get a list of supervisors for a project
    @GetMapping("/projects/{projectId}/supervisors")
    List<String> getSupervisorsForProject(@PathVariable("projectId") String projectId);

    // Method to get projects by employee ID
    @GetMapping("/projects/employee/{employeeId}")
    List<ProjectResponse> getProjectsByEmployeeId(@PathVariable("employeeId") String employeeId);
    
    
}
