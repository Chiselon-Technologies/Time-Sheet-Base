package com.create.project.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.create.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, String>{

	  List<Project> findByEmployeeTeamMembersContains(String employeeId);
	    List<Project> findBySupervisorTeamMembersContains(String supervisorId);
	
	
	
}
