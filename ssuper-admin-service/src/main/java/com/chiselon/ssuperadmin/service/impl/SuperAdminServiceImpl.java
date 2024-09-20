package com.chiselon.ssuperadmin.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chiselon.ssuperadmin.model.ArchiveSuperAdmin;
import com.chiselon.ssuperadmin.model.SuperAdmin;
import com.chiselon.ssuperadmin.repository.ArchiveSuperAdminRepository;
import com.chiselon.ssuperadmin.repository.SuperAdminRepository;
import com.chiselon.ssuperadmin.service.SuperAdminService;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    private SuperAdminRepository superAdminRepository;
    
    @Autowired
    private ArchiveSuperAdminRepository archiveSuperAdminRepository;

    @Autowired
    private ModelMapper modelMapper;  // Inject ModelMapper


    private long lastSuperAdminId = 0; // Track last SuperAdmin ID

    @Override
    public SuperAdmin createSuperAdmin(SuperAdmin superadmin) {
        validateSuperAdminFields(superadmin);

        // Check for duplicate entries before proceeding with creation
        checkForDuplicateEntries(superadmin);

        // Generate SuperAdmin ID if not provided
        if (superadmin.getSuperAdminId() == null || superadmin.getSuperAdminId().isEmpty()) {
            superadmin.setSuperAdminId(generateSuperAdminId());
        }

        // Save and return the created SuperAdmin
        return superAdminRepository.save(superadmin);
    }

    private void validateSuperAdminFields(SuperAdmin superadmin) {
        if (superadmin.getMobileNumber() != null && !String.valueOf(superadmin.getMobileNumber()).matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number must be exactly 10 digits.");
        }

        if (superadmin.getAadharNumber() != null && !String.valueOf(superadmin.getAadharNumber()).matches("\\d{12}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar number must be exactly 12 digits.");
        }
    }

    private void checkForDuplicateEntries(SuperAdmin superadmin) {
        if (superAdminRepository.existsByEmailId(superadmin.getEmailId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already in use.");
        }
        if (superadmin.getMobileNumber() != null && superAdminRepository.existsByMobileNumber(superadmin.getMobileNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number already in use.");
        }
        if (superadmin.getAadharNumber() != null && superAdminRepository.existsByAadharNumber(superadmin.getAadharNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar card number already in use.");
        }
        if (superAdminRepository.existsByPanNumber(superadmin.getPanNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN card number already in use.");
        }
    }

    private String generateSuperAdminId() {
        lastSuperAdminId++;
        return "SA" + String.format("%03d", lastSuperAdminId);
    }

    @Override
    public SuperAdmin updateSuperAdmin(String superAdminId, SuperAdmin superadmin) {
        SuperAdmin existingSuperAdmin = superAdminRepository.findBySuperAdminId(superAdminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SuperAdmin not found with ID: " + superAdminId));

        validateSuperAdminFields(superadmin);
        validateSuperAdminUpdate(superadmin, existingSuperAdmin);

        existingSuperAdmin.setFirstName(superadmin.getFirstName());
        existingSuperAdmin.setLastName(superadmin.getLastName());
        existingSuperAdmin.setAddress(superadmin.getAddress());
        existingSuperAdmin.setMobileNumber(superadmin.getMobileNumber());
        existingSuperAdmin.setEmailId(superadmin.getEmailId());
        existingSuperAdmin.setAadharNumber(superadmin.getAadharNumber());
        existingSuperAdmin.setPanNumber(superadmin.getPanNumber());
        existingSuperAdmin.setPassword(superadmin.getPassword());

        return superAdminRepository.save(existingSuperAdmin);
    }

    private void validateSuperAdminUpdate(SuperAdmin updatedSuperAdmin, SuperAdmin existingSuperAdmin) {
        if (!updatedSuperAdmin.getEmailId().equals(existingSuperAdmin.getEmailId()) &&
            superAdminRepository.existsByEmailId(updatedSuperAdmin.getEmailId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already in use.");
        }

        if (updatedSuperAdmin.getMobileNumber() != null && 
            !updatedSuperAdmin.getMobileNumber().equals(existingSuperAdmin.getMobileNumber()) &&
            superAdminRepository.existsByMobileNumber(updatedSuperAdmin.getMobileNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number already in use.");
        }

        if (updatedSuperAdmin.getAadharNumber() != null &&
            !updatedSuperAdmin.getAadharNumber().equals(existingSuperAdmin.getAadharNumber()) &&
            superAdminRepository.existsByAadharNumber(updatedSuperAdmin.getAadharNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar card number already in use.");
        }

        if (!updatedSuperAdmin.getPanNumber().equals(existingSuperAdmin.getPanNumber()) &&
            superAdminRepository.existsByPanNumber(updatedSuperAdmin.getPanNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN card number already in use.");
        }
    }

    @Override
    public void deleteSuperAdmin(String superAdminId) {
        // Check if the SuperAdmin exists
        SuperAdmin superAdmin = superAdminRepository.findBySuperAdminId(superAdminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SuperAdmin not found with ID: " + superAdminId));

        // Convert SuperAdmin to ArchiveSuperAdmin using ModelMapper
        ArchiveSuperAdmin archiveSuperAdmin = modelMapper.map(superAdmin, ArchiveSuperAdmin.class);

        // Save the archived record
        archiveSuperAdminRepository.save(archiveSuperAdmin);

        // Delete the SuperAdmin record from the main table
        superAdminRepository.deleteBySuperAdminId(superAdminId);
    }

    @Override
    public SuperAdmin getSuperAdminById(String superAdminId) {
        return superAdminRepository.findBySuperAdminId(superAdminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SuperAdmin not found with ID: " + superAdminId));
    }

    @Override
    public SuperAdmin validateSuperAdminCredentials(String emailId, String password) {
        SuperAdmin superAdmin = superAdminRepository.findByEmailIdAndPassword(emailId, password);
        if (superAdmin == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        }
        return superAdmin;
    }

    @Override
    public List<SuperAdmin> getAllSuperAdmins() {
        return superAdminRepository.findAll();
    }
}
