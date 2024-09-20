package com.leave.request.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leave.request.client.ProjectServiceClient;
import com.leave.request.entity.LeaveRequest;
import com.leave.request.entity.ProjectResponse;
import com.leave.request.repo.LeaveRequestRepository;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private ProjectServiceClient projectServiceClient;

    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date must be today or in the future.");
        }

        // Get the list of projects for the employee
        List<ProjectResponse> projects = projectServiceClient.getProjectsByEmployeeId(leaveRequest.getEmployeeId());

        // Find the project ID from the projects list (assuming only one project is associated with the employee)
        String projectId = projects.stream()
                                   .findFirst()
                                   .map(ProjectResponse::getProjectId)
                                   .orElseThrow(() -> new IllegalArgumentException("No project found for the employee."));

        // Get the list of supervisors for the project
        List<String> supervisors = projectServiceClient.getSupervisorsForProject(projectId);

        leaveRequest.setProjectId(projectId);
        leaveRequest.setSupervisors(supervisors);
        leaveRequest.setStatus("PENDING");

        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest updateLeaveRequest(Long id, LeaveRequest leaveRequest) {
        Optional<LeaveRequest> existingRequest = leaveRequestRepository.findById(id);
        if (existingRequest.isPresent()) {
            LeaveRequest request = existingRequest.get();
            if (request.getStartDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Cannot update a leave request with a past start date.");
            }
            request.setStartDate(leaveRequest.getStartDate());
            request.setEndDate(leaveRequest.getEndDate());
            request.setNoOfDays(leaveRequest.getNoOfDays());
            request.setReason(leaveRequest.getReason());
            request.setComments(leaveRequest.getComments());
            request.setStatus(leaveRequest.getStatus());
            request.setApprovedBy(leaveRequest.getApprovedBy());
            request.setRejectedBy(leaveRequest.getRejectedBy());
            request.setReasonForRejection(leaveRequest.getReasonForRejection());
            return leaveRequestRepository.save(request);
        } else {
            throw new IllegalArgumentException("Leave request not found.");
        }
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
    }

    public void deleteLeaveRequest(Long id) {
        leaveRequestRepository.deleteById(id);
    }

    public List<LeaveRequest> getLeaveRequestsByEmpId(String employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

//    public List<LeaveRequest> getLeaveRequestsBySupervisorId(String supervisorId) {
//        return leaveRequestRepository.findBySupervisorsContaining(supervisorId);
//    }
    
    public List<LeaveRequest> getLeaveRequestsBySupervisorId(String supervisorId) {
        // Fetch all leave requests from the repository
        List<LeaveRequest> allLeaveRequests = leaveRequestRepository.findAll();
        
        // Filter the leave requests where the supervisor is responsible for the employee
        return allLeaveRequests.stream()
                .filter(leaveRequest -> projectServiceClient.isSupervisorForEmployee(supervisorId, leaveRequest.getEmployeeId()))
                .collect(Collectors.toList());
    }
    

    public LeaveRequest approveLeaveRequest(Long id, String supervisorId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        if (!projectServiceClient.isSupervisorForEmployee(supervisorId, leaveRequest.getEmployeeId())) {
            throw new IllegalArgumentException("Supervisor not authorized to approve this leave request.");
        }
        leaveRequest.setStatus("APPROVED");
        leaveRequest.setApprovedBy(supervisorId);
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest rejectLeaveRequest(Long id, String supervisorId, String reason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found."));
        if (!projectServiceClient.isSupervisorForEmployee(supervisorId, leaveRequest.getEmployeeId())) {
            throw new IllegalArgumentException("Supervisor not authorized to reject this leave request.");
        }
        leaveRequest.setStatus("REJECTED");
        leaveRequest.setRejectedBy(supervisorId);
        leaveRequest.setReasonForRejection(reason);
        return leaveRequestRepository.save(leaveRequest);
    }
    
    
    
    // Approve multiple leave requests
    public List<LeaveRequest> approveMultipleLeaveRequests(List<Long> leaveRequestIds, String supervisorId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllById(leaveRequestIds);

        // Filter leave requests that the supervisor is authorized to approve
        List<LeaveRequest> authorizedRequests = leaveRequests.stream()
            .filter(request -> projectServiceClient.isSupervisorForEmployee(supervisorId, request.getEmployeeId()))
            .collect(Collectors.toList());

        // Update the status of each leave request
        for (LeaveRequest request : authorizedRequests) {
            request.setStatus("APPROVED");
            request.setApprovedBy(supervisorId);
        }

        // Save all updated leave requests in batch
        return leaveRequestRepository.saveAll(authorizedRequests);
    }
    
    
    // Reject multiple leave requests
    public List<LeaveRequest> rejectMultipleLeaveRequests(List<Long> leaveRequestIds, String reason, String supervisorId) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllById(leaveRequestIds);

        // Filter leave requests that the supervisor is authorized to reject
        List<LeaveRequest> authorizedRequests = leaveRequests.stream()
            .filter(request -> projectServiceClient.isSupervisorForEmployee(supervisorId, request.getEmployeeId()))
            .collect(Collectors.toList());

        // Update the status and rejection reason of each leave request
        for (LeaveRequest request : authorizedRequests) {
            request.setStatus("REJECTED");
            request.setReasonForRejection(reason);
            request.setRejectedBy(supervisorId);
        }

        // Save all updated leave requests in batch
        return leaveRequestRepository.saveAll(authorizedRequests);
    }
    

    
}


