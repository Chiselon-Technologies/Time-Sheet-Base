package com.timesheet.superadmin.client;

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

import com.timesheet.superadmin.entity.EmployeeWorkingHours;
import com.timesheet.superadmin.entity.ProjectWorkingHours;
import com.timesheet.superadmin.entity.WorkingHour;


@FeignClient(name = "Admin", url = "http://localhost:8081/api/working-hours")
public interface EmployeeServiceClient {

    @GetMapping("/{employeeId}/range")
    List<WorkingHour> getWorkingHoursByEmployeeIdAndDateRange(@PathVariable String employeeId,
                                                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PutMapping("/approve/{id}")
    WorkingHour approveWorkingHour(@PathVariable Long id);

    @PutMapping("/reject/{id}")
    WorkingHour rejectWorkingHour(@PathVariable Long id, @RequestParam String reason);


    @PutMapping("/update")
    WorkingHour updateWorkingHour(@RequestBody WorkingHour workingHour);

    @PutMapping("/{employeeId}/approve-range")
    List<WorkingHour> approveWorkingHoursInRange(@PathVariable String employeeId,
                                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

//    @PutMapping("/{employeeId}/reject-range")
//    List<WorkingHour> rejectWorkingHoursInRange(@PathVariable String employeeId,
//                                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//                                                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    
    @PutMapping("/{employeeId}/reject-range")
    List<WorkingHour> rejectWorkingHoursInRange(@PathVariable String employeeId,
                                                @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                @RequestParam String reason);

    
    
    @DeleteMapping("/{employeeId}/delete-range")
    void deleteWorkingHoursInRange(@PathVariable String employeeId,
                                   @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/{employeeId}/projects")
    List<ProjectWorkingHours> getProjectWorkingHoursByEmployeeId(@PathVariable String employeeId);
    
    
    @GetMapping("/all/range")
    public ResponseEntity<Map<String, List<WorkingHour>>> getAllEmployeesWorkHoursInRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);
    
    
    
    @GetMapping("/all/new")
    public ResponseEntity<Map<String, EmployeeWorkingHours>> getAllNewEmployeesWorkHours();
}

