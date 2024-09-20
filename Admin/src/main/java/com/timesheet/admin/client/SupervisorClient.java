package com.timesheet.admin.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.timesheet.admin.entity.EmployeeWorkingHours;
import com.timesheet.admin.entity.LeaveRequest;
import com.timesheet.admin.entity.ProjectWorkingHours;
import com.timesheet.admin.entity.Supervisor;
import com.timesheet.admin.entity.SupervisorResponse;
import com.timesheet.admin.entity.WorkingHour;

@FeignClient(name = "Supervisor" ,url = "http://localhost:8086")
public interface SupervisorClient {
    @GetMapping("/supervisors/{supervisorId}")
    SupervisorResponse getSupervisorById(@PathVariable("supervisorId") String supervisorId);

    @PostMapping("/supervisors")
    void createSupervisor(@RequestBody Supervisor supervisor);

    @GetMapping("/supervisors")
    List<SupervisorResponse> getAllSupervisors();
    
    
    
    
//    ----------------  Supervisor leave request  Approval/rejectcion --------------------------------
    
    
    @GetMapping("supervisor/leave-requests")
    List<LeaveRequest> getAllLeaveRequests();

    @PutMapping("supervisor/leave-requests/{id}")
    LeaveRequest updateLeaveRequest(@RequestParam Long id, @RequestBody LeaveRequest leaveRequest);
    
    @GetMapping("supervisor/leave-requests/{id}")
    LeaveRequest getLeaveRequestById(@PathVariable("id") Long id);
    
    
    
//    ------------------ Supervisor Working Hours  Approval/rejectcion  -------------------------------
    
    
    
    @GetMapping("sup/api/working-hours/{employeeId}/range")
    List<WorkingHour> getWorkingHoursByEmployeeIdAndDateRange(@PathVariable String employeeId,
                                                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PutMapping("sup/api/working-hours/approve/{id}")
    WorkingHour approveWorkingHour(@PathVariable Long id);

    @PutMapping("sup/api/working-hours/reject/{id}")
    WorkingHour rejectWorkingHour(@PathVariable Long id, @RequestParam String reason);


    @PutMapping("sup/api/working-hours/update")
    WorkingHour updateWorkingHour(@RequestBody WorkingHour workingHour);

    @PutMapping("sup/api/working-hours/{employeeId}/approve-range")
    List<WorkingHour> approveWorkingHoursInRange(@PathVariable String employeeId,
                                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PutMapping("sup/api/working-hours/{employeeId}/reject-range")
    List<WorkingHour> rejectWorkingHoursInRange(@PathVariable String employeeId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason);


    @DeleteMapping("sup/api/working-hours/{employeeId}/delete-range")
    void deleteWorkingHoursInRange(@PathVariable String employeeId,
                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("sup/api/working-hours/{employeeId}/projects")
    List<ProjectWorkingHours> getProjectWorkingHoursByEmployeeId(@PathVariable String employeeId);
    
    
    @GetMapping("sup/api/working-hours/all/range")
    public ResponseEntity<Map<String, List<WorkingHour>>> getAllEmployeesWorkHoursInRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    
    
    @GetMapping("sup/api/working-hours/all/new")
    public ResponseEntity<Map<String, EmployeeWorkingHours>> getAllNewEmployeesWorkHours();
    
    
}
