package com.timesheet.superadmin.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.timesheet.superadmin.client.AdminServiceClient;
import com.timesheet.superadmin.client.EmployeeServiceClient;
import com.timesheet.superadmin.controller.AdminNotFoundException;
import com.timesheet.superadmin.controller.InvalidRequestException;
import com.timesheet.superadmin.entity.AdminEntity;
import com.timesheet.superadmin.entity.ArchiveAdmin;
import com.timesheet.superadmin.entity.EmployeeWorkingHours;
import com.timesheet.superadmin.entity.LeaveRequest;
import com.timesheet.superadmin.entity.WorkingHour;
import com.timesheet.superadmin.repo.AdminRepository;
import com.timesheet.superadmin.repo.ArchiveAdminRepository;
import com.timesheet.superadmin.repo.WorkingHourRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private ArchiveAdminRepository archiveAdminRepository;

    @Autowired
    private ModelMapper modelMapper;


    private long lastAdminId = 0; // Track last Admin ID

    @Override
    public AdminEntity createAdmin(AdminEntity admin) {
        validateAdminFields(admin);

        // Check for duplicate entries before proceeding with creation
        checkForDuplicateEntries(admin);

        // Generate Admin ID if not provided
        if (admin.getAdminId() == null || admin.getAdminId().isEmpty()) {
            admin.setAdminId(generateAdminId());
        }

        // Save and return the created AdminEntity
        return adminRepository.save(admin);
    }

    private void validateAdminFields(AdminEntity admin) {
        if (admin.getMobileNumber() != null && !String.valueOf(admin.getMobileNumber()).matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number must be exactly 10 digits.");
        }

        if (admin.getAadharNumber() != null && !String.valueOf(admin.getAadharNumber()).matches("\\d{12}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar number must be exactly 12 digits.");
        }
    }

    private void checkForDuplicateEntries(AdminEntity admin) {
        if (adminRepository.existsByEmailId(admin.getEmailId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already in use.");
        }
        if (admin.getMobileNumber() != null && adminRepository.existsByMobileNumber(admin.getMobileNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number already in use.");
        }
        if (admin.getAadharNumber() != null && adminRepository.existsByAadharNumber(admin.getAadharNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar number already in use.");
        }
        if (adminRepository.existsByPanNumber(admin.getPanNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN card number already in use.");
        }
    }

    private String generateAdminId() {
        lastAdminId++;
        return "AD" + String.format("%03d", lastAdminId);
    }

    @Override
    public AdminEntity updateAdmin(String adminId, AdminEntity admin) {
        AdminEntity existingAdmin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with ID: " + adminId));

        validateAdminFields(admin);
        validateAdminUpdate(admin, existingAdmin);

        existingAdmin.setFirstName(admin.getFirstName());
        existingAdmin.setLastName(admin.getLastName());
        existingAdmin.setAddress(admin.getAddress());
        existingAdmin.setMobileNumber(admin.getMobileNumber());
        existingAdmin.setEmailId(admin.getEmailId());
        existingAdmin.setAadharNumber(admin.getAadharNumber());
        existingAdmin.setPanNumber(admin.getPanNumber());
        existingAdmin.setPassword(admin.getPassword());

        return adminRepository.save(existingAdmin);
    }

    private void validateAdminUpdate(AdminEntity updatedAdmin, AdminEntity existingAdmin) {
        if (!updatedAdmin.getEmailId().equals(existingAdmin.getEmailId()) &&
            adminRepository.existsByEmailId(updatedAdmin.getEmailId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already in use.");
        }

        if (updatedAdmin.getMobileNumber() != null && 
            !updatedAdmin.getMobileNumber().equals(existingAdmin.getMobileNumber()) &&
            adminRepository.existsByMobileNumber(updatedAdmin.getMobileNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number already in use.");
        }

        if (updatedAdmin.getAadharNumber() != null &&
            !updatedAdmin.getAadharNumber().equals(existingAdmin.getAadharNumber()) &&
            adminRepository.existsByAadharNumber(updatedAdmin.getAadharNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar card number already in use.");
        }

        if (!updatedAdmin.getPanNumber().equals(existingAdmin.getPanNumber()) &&
            adminRepository.existsByPanNumber(updatedAdmin.getPanNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN card number already in use.");
        }
    }

//    @Override
//    public void deleteAdmin(String adminId) {
//        if (!adminRepository.existsByAdminId(adminId)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with ID: " + adminId);
//        }
//        adminRepository.deleteByAdminId(adminId);
//    }
    
    @Override
    public void deleteAdmin(String adminId) {
    	// Check if the Admin exists
        AdminEntity admin = adminRepository.findByAdminId(adminId)
        		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SuperAdmin not found with ID: " + adminId));

        // Convert SuperAdmin to ArchiveSuperAdmin using ModelMapper
        ArchiveAdmin archiveSuperAdmin = modelMapper.map(admin, ArchiveAdmin.class);

        // Save the archived record
        archiveAdminRepository.save(archiveSuperAdmin);

        // Delete the SuperAdmin record from the main table
        adminRepository.deleteById(adminId);
    }

    @Override
    public AdminEntity getAdminById(String adminId) {
        return adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found with ID: " + adminId));
    }
    
    @Override
    public AdminEntity getPermissions(String adminId) {
        return adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + adminId));
    }

    @Override
    public AdminEntity validateAdminCredentials(String emailId, String password) {
        AdminEntity admin = adminRepository.findByEmailIdAndPassword(emailId, password);
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }
        return admin;
    }

    @Override
    public List<AdminEntity> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public List<AdminEntity> findByFirstName(String firstName) {
        List<AdminEntity> admins = adminRepository.findByFirstName(firstName);
        if (admins.isEmpty()) {
            throw new AdminNotFoundException("Admin with first name: " + firstName + " not found.");
        }
        return admins;
    }
	
	
	
	
	
	
	
	
	
	
    @Autowired
    private AdminServiceClient adminServiceClient;

 public List<LeaveRequest> getAllLeaveRequests() {
        return adminServiceClient.getAllLeaveRequests();
    }

    public LeaveRequest approveLeaveRequest(Long id) {
        LeaveRequest leaveRequest = adminServiceClient.getAllLeaveRequests().stream()
                .filter(request -> request.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        leaveRequest.setStatus("APPROVED");
        return adminServiceClient.updateLeaveRequest(id, leaveRequest);
    }

//    public LeaveRequest rejectLeaveRequest(Long id) {
//        LeaveRequest leaveRequest = adminServiceClient.getAllLeaveRequests().stream()
//                .filter(request -> request.getId().equals(id))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
//        leaveRequest.setStatus("REJECTED");
//        return adminServiceClient.updateLeaveRequest(id, leaveRequest);
//    }
    
    
    public LeaveRequest rejectLeaveRequest(Long id, String reasonforRejection) {
        LeaveRequest leaveRequest = adminServiceClient.getAllLeaveRequests().stream()
                .filter(request -> request.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        leaveRequest.setStatus("REJECTED");
        leaveRequest.setReasonForRejection(reasonforRejection);
        return adminServiceClient.updateLeaveRequest(id, leaveRequest);
    }
    
    
    public List<LeaveRequest> approveMultipleLeaveRequests(List<Long> ids) {
        List<LeaveRequest> leaveRequests = adminServiceClient.getAllLeaveRequests();
        List<LeaveRequest> approvedRequests = new ArrayList<>();
        
        for (Long id : ids) {
            LeaveRequest leaveRequest = leaveRequests.stream()
                    .filter(request -> request.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Leave Request ID: " + id));
            leaveRequest.setStatus("APPROVED");
            approvedRequests.add(adminServiceClient.updateLeaveRequest(id, leaveRequest));
        }
        
        return approvedRequests;
    }

    public List<LeaveRequest> rejectMultipleLeaveRequests(List<Long> ids, String reasonForRejection) {
        List<LeaveRequest> leaveRequests = adminServiceClient.getAllLeaveRequests();
        List<LeaveRequest> rejectedRequests = new ArrayList<>();
        
        for (Long id : ids) {
            LeaveRequest leaveRequest = leaveRequests.stream()
                    .filter(request -> request.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Leave Request ID: " + id));
            leaveRequest.setStatus("REJECTED");
            leaveRequest.setReasonForRejection(reasonForRejection);
            rejectedRequests.add(adminServiceClient.updateLeaveRequest(id, leaveRequest));
        }
        
        return rejectedRequests;
    }


}