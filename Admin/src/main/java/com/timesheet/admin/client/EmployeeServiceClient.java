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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.timesheet.admin.entity.EmployeeResponse;
import com.timesheet.admin.entity.EmployeeWorkingHours;
import com.timesheet.admin.entity.LeaveRequest;
import com.timesheet.admin.entity.ProjectWorkingHours;
import com.timesheet.admin.entity.WorkingHour;


@FeignClient(name = "Employee", url = "http://localhost:8002/employee")

//----------  Employee timesheet-------------------

//@FeignClient(name = "Employee", url = "http://localhost:8002")    
public interface EmployeeServiceClient {

	
	
//	----------------------- New Microservices -------------------
	
	
//----------------------Employee Project Creation -------------------------------	
	
	
	 @GetMapping("/{employeeId}")
	    EmployeeResponse getEmployeeById(@PathVariable("employeeId") String employeeId);

	    @DeleteMapping("/{employeeId}")
	    void deleteEmployee(@PathVariable("employeeId") String employeeId);

	    @GetMapping("/getemployees")
	    List<EmployeeResponse> getAllEmployees();
	
    
 
//------------------------ Employee Leave Request -------------------------------	
	
	
    @GetMapping("/leave-requests")
    List<LeaveRequest> getAllLeaveRequests();

    @PutMapping("/leave-requests/{id}")
    LeaveRequest updateLeaveRequest(@RequestParam Long id, @RequestBody LeaveRequest leaveRequest);
    
    @GetMapping("/leave-requests/{id}")
    LeaveRequest getLeaveRequestById(@PathVariable("id") Long id);
    
    
//------------------ TIMESHEET APPROVAL OF EMPLOYEES -----------------------
    
    
 
    @GetMapping("api/working-hours/{employeeId}/range")
    List<WorkingHour> getWorkingHoursByEmployeeIdAndDateRange(@PathVariable String employeeId,
                                                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PutMapping("api/working-hours/approve/{id}")
    WorkingHour approveWorkingHour(@PathVariable Long id);

    @PutMapping("api/working-hours/reject/{id}")
    WorkingHour rejectWorkingHour(@PathVariable Long id);

    @PutMapping("api/working-hours/update")
    WorkingHour updateWorkingHour(@RequestBody WorkingHour workingHour);

    @PutMapping("api/working-hours/{employeeId}/approve-range")
    List<WorkingHour> approveWorkingHoursInRange(@PathVariable String employeeId,
                                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PutMapping("api/working-hours/{employeeId}/reject-range")
    List<WorkingHour> rejectWorkingHoursInRange(@PathVariable String employeeId,
                                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @DeleteMapping("api/working-hours/{employeeId}/delete-range")
    void deleteWorkingHoursInRange(@PathVariable String employeeId,
                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("api/working-hours/{employeeId}/projects")
    List<ProjectWorkingHours> getProjectWorkingHoursByEmployeeId(@PathVariable String employeeId);
    
    
    @GetMapping("api/working-hours/all/range")
    public ResponseEntity<Map<String, List<WorkingHour>>> getAllEmployeesWorkHoursInRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
  
   
    
}
