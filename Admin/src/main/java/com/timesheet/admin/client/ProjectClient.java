package com.timesheet.admin.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.timesheet.admin.entity.Project;
import com.timesheet.admin.entity.ProjectResponse;

@FeignClient(name = "Project", url = "http://localhost:8085")
public interface ProjectClient {
    @PostMapping("/projects")
    ProjectResponse createProject(@RequestBody Project project);
    

    @GetMapping("/projects/{projectId}")
    ProjectResponse getProjectById(@PathVariable("projectId") String projectId);

    @PutMapping("/projects/{projectId}")
    ProjectResponse updateProject(@PathVariable("projectId") String projectId, @RequestBody Project updatedProject);

    @DeleteMapping("/projects/{projectId}")
    void deleteProject(@PathVariable("projectId") String projectId);

    @GetMapping("/projects")
    List<ProjectResponse> getAllProjects();
   
}
