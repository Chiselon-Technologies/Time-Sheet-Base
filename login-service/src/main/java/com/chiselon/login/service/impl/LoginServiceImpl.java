package com.chiselon.login.service.impl;

import com.chiselon.login.client.AdminServiceClient;
import com.chiselon.login.client.SSuperAdminServiceClient;
import com.chiselon.login.client.SuperAdminServiceClient;
import com.chiselon.login.client.SupervisorServiceClient;
import com.chiselon.login.model.Admin;
import com.chiselon.login.model.Employee;
import com.chiselon.login.model.SuperAdmin;
import com.chiselon.login.model.Supervisor;
import com.chiselon.login.service.LoginService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SuperAdminServiceClient superAdminServiceClient;

    @Autowired
    private AdminServiceClient adminServiceClient;

    @Autowired
    private SSuperAdminServiceClient ssuperAdminServiceClient;
    
    @Autowired
    private SupervisorServiceClient supervisorServiceClient; // New Feign client for Supervisor

    @Override
    public Map<String, Object> loginAsAdmin(String emailId, String password) {
        // Call to super-admin-service to validate credentials
        Admin adminDetails = superAdminServiceClient.validateAdminCredentials(emailId, password);

        Map<String, Object> response = new HashMap<>();
        if (adminDetails != null) {
            response.put("adminId", adminDetails.getAdminId());
            response.put("emailId", adminDetails.getEmailId());
            response.put("message", "Admin login successful");
        } else {
            response.put("adminId", null);
            response.put("emailId", null);
            response.put("message", "Invalid admin credentials");
        }
        return response;
    }

    @Override
    public Map<String, Object> loginAsEmployee(String emailId, String password) {
    	// Call to admin-service to validate credentials
        Employee employeeDetails = adminServiceClient.validateEmployeeCredentials(emailId, password);

        Map<String, Object> response = new HashMap<>();
        if (employeeDetails != null) {
            response.put("employeeId", employeeDetails.getEmployeeId());
            response.put("emailId", employeeDetails.getEmailId());
            response.put("message", "Employee login successful");
        } else {
            response.put("adminId", null);
            response.put("emailId", null);
            response.put("message", "Invalid Employee credentials");
        }
        return response;
    }

	@Override
	public Map<String, Object> loginAsSuperAdmin(String emailId, String password) {
		// TODO Auto-generated method stub
		// Call to Ssuper-admin-service to validate credentials
        SuperAdmin superadminDetails = ssuperAdminServiceClient.validateSuperAdminCredentials(emailId, password);

        Map<String, Object> response = new HashMap<>();
        if (superadminDetails != null) {
            response.put("superAdminId", superadminDetails.getSuperAdminId());
            response.put("emailId", superadminDetails.getEmailId());
            response.put("message", "SuperAdmin login successful");
        } else {
            response.put("superAdminId", null);
            response.put("emailId", null);
            response.put("message", " Invalid SuperAdmin credentials");
        }
        return response;
	}

	@Override
    public Map<String, Object> loginAsSupervisor(String emailId, String password) {
        Supervisor supervisorDetails = supervisorServiceClient.validateSupervisorCredentials(emailId, password);

        Map<String, Object> response = new HashMap<>();
        if (supervisorDetails != null) {
            response.put("supervisorId", supervisorDetails.getSupervisorId());
            response.put("emailId", supervisorDetails.getEmailId());
            response.put("message", "Supervisor login successful");
        } else {
            response.put("supervisorId", null);
            response.put("emailId", null);
            response.put("message", "Invalid Supervisor credentials");
        }
        return response;
    }
}
