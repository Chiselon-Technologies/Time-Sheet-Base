package com.timesheet.supervisor.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.timesheet.supervisor.entity.Supervisor;
import com.timesheet.supervisor.service.SupervisorService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/supervisors")
public class SupervisorController {

    @Autowired
    private SupervisorService supervisorService;

    @PostMapping
    public ResponseEntity<Supervisor> createSupervisor(@RequestBody Supervisor supervisor) {
        try {
            Supervisor createdSupervisor = supervisorService.createSupervisor(supervisor);
            return new ResponseEntity<>(createdSupervisor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{supervisorId}")
    public ResponseEntity<Void> deleteSupervisor(@PathVariable String supervisorId) {
        try {
            supervisorService.deleteSupervisor(supervisorId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{supervisorId}")
    public ResponseEntity<Supervisor> getSupervisorById(@PathVariable String supervisorId) {
        Supervisor supervisor = supervisorService.getSupervisorById(supervisorId);
        if (supervisor != null) {
            return new ResponseEntity<>(supervisor, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Supervisor>> getAllSupervisors() {
        List<Supervisor> supervisors = supervisorService.getAllSupervisors();
        return new ResponseEntity<>(supervisors, HttpStatus.OK);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Supervisor> validateEmployeeCredentials(
            @RequestParam String emailId,
            @RequestParam String password) {
    	Supervisor supervisor = supervisorService.validateSupervisorCredentials(emailId, password);
        if (supervisor != null) {
            return new ResponseEntity<>(supervisor, HttpStatus.OK); // 200 OK for valid credentials
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized for invalid credentials
        }
    }

    
    @PutMapping("/{supervisorId}")
    public ResponseEntity<Supervisor> updateSupervisor(@PathVariable String supervisorId, @RequestBody Supervisor updatedSupervisor) {
        Supervisor supervisor = supervisorService.updateSupervisor(supervisorId, updatedSupervisor);
        if (supervisor != null) {
            return new ResponseEntity<>(supervisor, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
