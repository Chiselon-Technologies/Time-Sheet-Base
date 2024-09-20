package com.timesheet.supervisor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Supervisor {

    @Id
    private String supervisorId;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @NotNull(message = "Mobile number must not be null")
    private Long mobileNumber;

    @NotBlank(message = "Email ID must not be blank")
    @Pattern(regexp = ".*@gmail\\.com$", message = "Email address must end with @gmail.com")
    private String emailId;

    @NotNull(message = "Aadhar number must not be null")
    private Long aadharNumber;

    @NotBlank(message = "PAN number must not be blank")
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "PAN number must have the format ABCDE1234F")
    private String panNumber;

    @NotBlank(message = "Password must not be blank")
    private String password;
    
    @ElementCollection
    private List<String> projects;
}
