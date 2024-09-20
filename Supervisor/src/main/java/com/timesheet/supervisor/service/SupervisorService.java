package com.timesheet.supervisor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.timesheet.supervisor.entity.Supervisor;
import com.timesheet.supervisor.exceptions.InvalidCredentialsException;
import com.timesheet.supervisor.exceptions.SupervisorNotFoundException;
import com.timesheet.supervisor.repo.SupervisorRepository;

@Service
public class SupervisorService {

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ID_GENERATION_SERVICE_URL = "http://localhost:9090/api/id-generation/generate-supervisor-id";

    public Supervisor createSupervisor(Supervisor supervisor) {
    	if (supervisor.getSupervisorId() == null || supervisor.getSupervisorId().isEmpty()) {
            supervisor.setSupervisorId(restTemplate.getForObject(ID_GENERATION_SERVICE_URL, String.class));
        }

        return supervisorRepository.save(supervisor);
    }
    
    public Supervisor updateSupervisor(String supervisorId, Supervisor updatedSupervisor) {
    	 Supervisor existingSupervisor = supervisorRepository.findById(supervisorId)
                 .orElseThrow(() -> new SupervisorNotFoundException("Supervisor not found with ID: " + supervisorId));


        // Update fields
        existingSupervisor.setFirstName(updatedSupervisor.getFirstName());
        existingSupervisor.setLastName(updatedSupervisor.getLastName());
        existingSupervisor.setAddress(updatedSupervisor.getAddress());
        existingSupervisor.setMobileNumber(updatedSupervisor.getMobileNumber());
        existingSupervisor.setEmailId(updatedSupervisor.getEmailId());
        existingSupervisor.setAadharNumber(updatedSupervisor.getAadharNumber());
        existingSupervisor.setPanNumber(updatedSupervisor.getPanNumber());
        existingSupervisor.setPassword(updatedSupervisor.getPassword());
        existingSupervisor.setProjects(updatedSupervisor.getProjects());

        return supervisorRepository.save(existingSupervisor);
    }

    public Supervisor getSupervisorById(String supervisorId) {
    	return supervisorRepository.findById(supervisorId)
                .orElseThrow(() -> new SupervisorNotFoundException("Supervisor not found with ID: " + supervisorId));
    }
    
    public void deleteSupervisor(String supervisorId) {
        supervisorRepository.deleteById(supervisorId);
    }

    public List<Supervisor> getAllSupervisors() {
        return supervisorRepository.findAll();
    }
    
    public Supervisor validateSupervisorCredentials(String emailId, String password) {
        return supervisorRepository.findByEmailIdAndPassword(emailId, password)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials provided."));
    }
}
