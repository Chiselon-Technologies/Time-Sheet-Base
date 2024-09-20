package com.timesheet.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;


import com.timesheet.admin.client.EmployeeServiceClient;
import com.timesheet.admin.client.ProjectClient;
import com.timesheet.admin.client.SuperAdminServiceClient;
import com.timesheet.admin.client.SupervisorClient;
import com.timesheet.admin.entity.AdminEntity;
import com.timesheet.admin.entity.EmployeeResponse;
import com.timesheet.admin.entity.Project;
import com.timesheet.admin.entity.ProjectResponse;
import com.timesheet.admin.entity.SupervisorResponse;
import com.timesheet.admin.repo.ProjectRepository;

@Service
public class AdminProjectService {

	@Autowired
	private  ProjectRepository projectRepository;
	 @Autowired
	    private EmployeeServiceClient employeeClient;

	    @Autowired
	    private SupervisorClient supervisorClient;

	    @Autowired
	    private ProjectClient projectClient;

	    @Autowired
	    private  SuperAdminServiceClient superAdminServiceClient;

	    
	    
	    public ProjectResponse createProject(Project project, String adminId) {
	        // Check if the admin has permission to create projects
	        if (!hasPermission(adminId, "CREATE_PROJECT")) {
	            throw new SecurityException("Admin does not have permission to create projects");
	        }

	        return projectClient.createProject(project);
	    }
	    
	   

	    public ProjectResponse getProjectById(String projectId) {
	        return projectClient.getProjectById(projectId);
	    }

	    public ProjectResponse updateProject(String projectId, Project updatedProject, String adminId) {
	        if (!hasPermission(adminId, "EDIT_PROJECT")) {
	            throw new SecurityException("Admin does not have permission to edit projects");
	        }

	        // Logic to update the project, handling Employee and Supervisor lists
	        return projectClient.updateProject(projectId, updatedProject);
	    }

	    public void deleteProject(String projectId, String adminId) {
	    	
	    	 if (!hasPermission(adminId, "DELETE_PROJECT")) {
	             throw new SecurityException("Admin does not have permission to delete projects");
	         }
	        projectClient.deleteProject(projectId);
	    }

	    public List<ProjectResponse> getAllProjects() {
	        return projectClient.getAllProjects();
	    }

	    public List<EmployeeResponse> getAllEmployees() {
	        List<EmployeeResponse> employees = employeeClient.getAllEmployees();
	        return employees.stream().map(this::populateEmployeeDetails).collect(Collectors.toList());
	    }

	    private EmployeeResponse populateEmployeeDetails(EmployeeResponse employee) {
	        // Fetch projects involving the employee
	        List<ProjectResponse> projects = projectClient.getAllProjects();
	        List<String> projectIds = projects.stream()
	                .filter(project -> project.getEmployeeTeamMembers().stream()
	                        .anyMatch(member -> member.getEmployeeId().equals(employee.getEmployeeId())))
	                .map(ProjectResponse::getProjectId)
	                .collect(Collectors.toList());
	        employee.setProjects(projectIds);

	        // Fetch supervisors for the employee
	        List<String> supervisorIds = projects.stream()
	                .filter(project -> project.getEmployeeTeamMembers().stream()
	                        .anyMatch(member -> member.getEmployeeId().equals(employee.getEmployeeId())))
	                .flatMap(project -> project.getSupervisorTeamMembers().stream()
	                        .map(SupervisorResponse::getSupervisorId))
	                .distinct()
	                .collect(Collectors.toList());
	        employee.setSupervisor(supervisorIds);

	        return employee;
	    }



	    public List<SupervisorResponse> getAllSupervisors() {
	        List<SupervisorResponse> supervisors = supervisorClient.getAllSupervisors();
	        return supervisors.stream().map(this::populateSupervisorDetails).collect(Collectors.toList());
	    }

	    private SupervisorResponse populateSupervisorDetails(SupervisorResponse supervisor) {
	        List<ProjectResponse> projects = projectClient.getAllProjects();
	        List<String> projectIds = projects.stream()
	                .filter(project -> project.getSupervisorTeamMembers().stream()
	                        .anyMatch(member -> member.getSupervisorId().equals(supervisor.getSupervisorId())))
	                .map(ProjectResponse::getProjectId)
	                .collect(Collectors.toList());
	        supervisor.setProjects(projectIds);

	        return supervisor;
	    }
	    
	    
	    
	    
	    public EmployeeResponse getEmployeeById(String employeeId) {
	        EmployeeResponse employee = employeeClient.getEmployeeById(employeeId);
	        return populateEmployeeDetails(employee);
	    }
	    
	    
	    public SupervisorResponse getSupervisorById(String supervisorId) {
	        SupervisorResponse supervisor = supervisorClient.getSupervisorById(supervisorId);
	        return populateSupervisorDetails(supervisor);
	    }
	    
	    private boolean hasPermission(String adminId, String action) {
	        AdminEntity admin = superAdminServiceClient.getPermissions(adminId);
	        switch (action) {
	            case "CREATE_PROJECT":
	                return admin.isCanCreateProject();
	            case "EDIT_PROJECT":
	                return admin.isCanEditProject();
	            case "DELETE_PROJECT":
	                return admin.isCanDeleteProject();
	            default:
	                throw new IllegalArgumentException("Unknown action: " + action);
	        }
	    }

	    
}
