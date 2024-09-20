package com.timesheet.admin.entity;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

	
	private String employeeId;
	private String firstName;
	private String lastName;
    private String address;
    private Long mobileNumber;
    private String emailId;
    private Long aadharNumber;
    private String panNumber;
    private List<String> supervisor; // Nullable
    private SupervisorResponse supervisorId;
    private List<String> projects;

	
	
	
	
}
