package com.timesheet.employee.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.timesheet.employee.client.SuperAdminServiceClient;
import com.timesheet.employee.entity.AdminEntity;
import com.timesheet.employee.entity.ArchiveEmployee;
import com.timesheet.employee.entity.Employee;
import com.timesheet.employee.exception.AdminNotFoundException;
import com.timesheet.employee.exception.EmployeeNotFoundException;
import com.timesheet.employee.exception.InvalidCredentialsException;
import com.timesheet.employee.exception.InvalidRequestException;
import com.timesheet.employee.exception.PermissionDeniedException;
import com.timesheet.employee.repo.ArchiveEmployeeRepository;
import com.timesheet.employee.repo.EmployeeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EmployeeProjectService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SuperAdminServiceClient superAdminServiceClient;
    
    @Autowired
    private ArchiveEmployeeRepository archiveEmployeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    private long lastEmployeeId = 0; // Track last Employee ID

    public Employee createEmployee(Employee employee, String adminId) {
        if (!hasPermission(adminId, "CREATE_EMPLOYEE")) {
            throw new SecurityException("Admin does not have permission to create employees");
        }

        validateEmployeeFields(employee);
        checkForDuplicateEntries(employee);

        if (employee.getEmployeeId() == null || employee.getEmployeeId().isEmpty()) {
            employee.setEmployeeId(generateEmployeeId());
        }

        return employeeRepository.save(employee);
    }

    private void validateEmployeeFields(Employee employee) {
        // Validate mobile number
        if (employee.getMobileNumber() != null && !employee.getMobileNumber().toString().matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number must be exactly 10 digits.");
        }

        // Validate Aadhar number
        if (employee.getAadharNumber() != null && !employee.getAadharNumber().toString().matches("\\d{12}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar number must be exactly 12 digits.");
        }

        // Validate PAN number format
        if (employee.getPanNumber() != null && !employee.getPanNumber().matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN number must have the format ABCDE1234F.");
        }

        // Additional validations can be added here if needed
    }

    private void checkForDuplicateEntries(Employee employee) {
        if (employeeRepository.findByEmailId(employee.getEmailId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already in use.");
        }
        if (employeeRepository.findByMobileNumber(employee.getMobileNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number already in use.");
        }
        if (employeeRepository.findByAadharNumber(employee.getAadharNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar number already in use.");
        }
        if (employeeRepository.findByPanNumber(employee.getPanNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN number already in use.");
        }
    }

    private String generateEmployeeId() {
        lastEmployeeId++;
        return "EMP" + String.format("%03d", lastEmployeeId);
    }

    public Employee updateEmployee(String employeeId, Employee employee, String adminId) {
        if (!hasPermission(adminId, "EDIT_EMPLOYEE")) {
            throw new SecurityException("Admin does not have permission to edit employees");
        }

        Employee existingEmployee = employeeRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with ID: " + employeeId));

        validateEmployeeFields(employee);
        validateEmployeeUpdate(employee, existingEmployee);

        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setAddress(employee.getAddress());
        existingEmployee.setMobileNumber(employee.getMobileNumber());
        existingEmployee.setEmailId(employee.getEmailId());
        existingEmployee.setAadharNumber(employee.getAadharNumber());
        existingEmployee.setPanNumber(employee.getPanNumber());
        existingEmployee.setPassword(employee.getPassword());

        return employeeRepository.save(existingEmployee);
    }

    private void validateEmployeeUpdate(Employee updatedEmployee, Employee existingEmployee) {
        if (!updatedEmployee.getEmailId().equals(existingEmployee.getEmailId()) &&
            employeeRepository.findByEmailId(updatedEmployee.getEmailId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already in use.");
        }

        if (!updatedEmployee.getMobileNumber().equals(existingEmployee.getMobileNumber()) &&
            employeeRepository.findByMobileNumber(updatedEmployee.getMobileNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile number already in use.");
        }

        if (!updatedEmployee.getAadharNumber().equals(existingEmployee.getAadharNumber()) &&
            employeeRepository.findByAadharNumber(updatedEmployee.getAadharNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aadhar number already in use.");
        }

        if (!updatedEmployee.getPanNumber().equals(existingEmployee.getPanNumber()) &&
            employeeRepository.findByPanNumber(updatedEmployee.getPanNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAN number already in use.");
        }
    }

    public void deleteEmployee(String employeeId, String adminId) {
    	// Check for admin permissions
        if (!hasPermission(adminId, "DELETE_EMPLOYEE")) {
            throw new SecurityException("Admin does not have permission to delete employees");
        }

        // Check if the Employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with ID: " + employeeId));

        // Convert Employee to ArchiveEmployee using ModelMapper
        ArchiveEmployee archiveEmployee = modelMapper.map(employee, ArchiveEmployee.class);

        // Save the archived record
        archiveEmployeeRepository.save(archiveEmployee);

        // Delete the Employee record from the main table
        employeeRepository.deleteById(employeeId);
    }

    public Employee getEmployeeById(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with ID: " + employeeId));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> findByFirstName(String firstName) {
        List<Employee> employees = employeeRepository.findByFirstName(firstName);
        if (employees.isEmpty()) {
            throw new EmployeeNotFoundException("Employee with first name: " + firstName + " not found.");
        }
        return employees;
    }

    public Employee validateEmployeeCredentials(String emailId, String password) {
        return employeeRepository.findByEmailIdAndPassword(emailId, password)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials."));
    }

    public void deleteEmployeeProj(String employeeId) {
    	 // Retrieve the employee entity
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        // Convert Employee to ArchiveEmployee using ModelMapper
        ArchiveEmployee archiveEmployee = modelMapper.map(employee, ArchiveEmployee.class);

        // Save the archived employee record
        archiveEmployeeRepository.save(archiveEmployee);

        // Delete the employee record from the main table
        employeeRepository.delete(employee);
    }

    private boolean hasPermission(String adminId, String action) {
        AdminEntity admin = superAdminServiceClient.getPermissions(adminId);
        if (admin == null) {
            throw new AdminNotFoundException(adminId);
        }
        switch (action) {
            case "CREATE_EMPLOYEE":
                return admin.isCanCreateEmployee();
            case "EDIT_EMPLOYEE":
                return admin.isCanEditEmployee();
            case "DELETE_EMPLOYEE":
                return admin.isCanDeleteEmployee();
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }



    public void importExcelToDatabase(MultipartFile file) {
        List<Employee> employeesToSave = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // Fetch all existing employees once
        List<Employee> existingEmployees = employeeRepository.findAll();
        Set<String> existingEmails = new HashSet<>();
        Set<Long> existingMobileNumbers = new HashSet<>();
        Set<Long> existingAadharNumbers = new HashSet<>();
        Set<String> existingPANs = new HashSet<>();

        // Populate sets for quick lookup
        for (Employee emp : existingEmployees) {
            existingEmails.add(emp.getEmailId());
            existingMobileNumbers.add(emp.getMobileNumber());
            existingAadharNumbers.add(emp.getAadharNumber());
            existingPANs.add(emp.getPanNumber());
        }

        // Sets to track duplicates found in the Excel file
        Set<String> seenEmails = new HashSet<>();
        Set<Long> seenMobileNumbers = new HashSet<>();
        Set<Long> seenAadharNumbers = new HashSet<>();
        Set<String> seenPANs = new HashSet<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                try {
                    String firstName = row.getCell(0).getStringCellValue();
                    String lastName = row.getCell(1).getStringCellValue();
                    String address = row.getCell(2).getStringCellValue();
                    long mobileNumber = (long) row.getCell(3).getNumericCellValue();
                    String emailId = row.getCell(4).getStringCellValue();
                    long aadharNumber = (long) row.getCell(5).getNumericCellValue();
                    String panNumber = row.getCell(6).getStringCellValue();
                    String password = row.getCell(7).getStringCellValue();

                    // Check for duplicates in the seen set
                    if (!seenMobileNumbers.add(mobileNumber)) {
                        errorMessages.add("Duplicate mobile number in Excel: " + mobileNumber + " at row " + (row.getRowNum() + 1));
                    }
                    if (!seenEmails.add(emailId)) {
                        errorMessages.add("Duplicate email in Excel: " + emailId + " at row " + (row.getRowNum() + 1));
                    }
                    if (!seenAadharNumbers.add(aadharNumber)) {
                        errorMessages.add("Duplicate Aadhar number in Excel: " + aadharNumber + " at row " + (row.getRowNum() + 1));
                    }
                    if (!seenPANs.add(panNumber)) {
                        errorMessages.add("Duplicate PAN number in Excel: " + panNumber + " at row " + (row.getRowNum() + 1));
                    }

                    // If duplicates are found in the seen sets, stop processing and return errors
                    if (errorMessages.size() > 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errorMessages));
                    }

                    // Check against existing records in the database
                    if (existingMobileNumbers.contains(mobileNumber)) {
                        errorMessages.add("Mobile number " + mobileNumber + " already exists in the database.");
                    }
                    if (existingEmails.contains(emailId)) {
                        errorMessages.add("Email " + emailId + " already exists in the database.");
                    }
                    if (existingAadharNumbers.contains(aadharNumber)) {
                        errorMessages.add("Aadhar number " + aadharNumber + " already exists in the database.");
                    }
                    if (existingPANs.contains(panNumber)) {
                        errorMessages.add("PAN number " + panNumber + " already exists in the database.");
                    }

                    // If there are any errors, stop processing further
                    if (!errorMessages.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errorMessages));
                    }

                    // Create the new Employee object
                    Employee employee = new Employee();
                    employee.setFirstName(firstName);
                    employee.setLastName(lastName);
                    employee.setAddress(address);
                    employee.setMobileNumber(mobileNumber);
                    employee.setEmailId(emailId);
                    employee.setAadharNumber(aadharNumber);
                    employee.setPanNumber(panNumber);
                    employee.setPassword(password);

                    employeesToSave.add(employee);

                } catch (Exception e) {
                    errorMessages.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }

        // Handle errors
        if (!errorMessages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errorMessages));
        }

        // Save all valid employees
        for (Employee employee : employeesToSave) {
            try {
                employee.setEmployeeId(generateEmployeeId()); // Generate unique ID for each employee
                employeeRepository.save(employee);
            } catch (Exception e) {
                errorMessages.add("Failed to save employee " + employee.getEmailId() + ": " + e.getMessage());
            }
        }
    }


	
}
