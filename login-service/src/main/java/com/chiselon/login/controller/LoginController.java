package com.chiselon.login.controller;

import com.chiselon.login.service.LoginService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/admin")
    public ResponseEntity<Map<String, Object>> loginAsAdmin(@RequestParam String emailId,
                                                             @RequestParam String password) {
        Map<String, Object> response = loginService.loginAsAdmin(emailId, password);

        if ("Admin login successful".equals(response.get("message"))) {
            return ResponseEntity.ok(response); // Status code 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Status code 401 Unauthorized
        }
    }

    @PostMapping("/employee")
    public ResponseEntity<Map<String, Object>> loginAsEmployee(@RequestParam String emailId, 
                                                               @RequestParam String password) {
        Map<String, Object> response = loginService.loginAsEmployee(emailId, password);
        
        if ("Employee login successful".equals(response.get("message"))) {
            return ResponseEntity.ok(response); // Status code 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Status code 401 Unauthorized
        }
    }
    
    @PostMapping("/superadmin")
    public ResponseEntity<Map<String, Object>> loginAsSuperAdmin(@RequestParam String emailId, 
                                                                  @RequestParam String password) {
        Map<String, Object> response = loginService.loginAsSuperAdmin(emailId, password);

        if ("SuperAdmin login successful".equals(response.get("message"))) {
            return ResponseEntity.ok(response); // Status code 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Status code 401 Unauthorized
        }
    }
    
    @PostMapping("/supervisor")
    public ResponseEntity<Map<String, Object>> loginAsSupervisor(@RequestParam String emailId, 
                                                                  @RequestParam String password) {
        Map<String, Object> response = loginService.loginAsSupervisor(emailId, password);
        if ("Supervisor login successful".equals(response.get("message"))) {
            return ResponseEntity.ok(response); // Status code 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Status code 401 Unauthorized
        }
    }
}
