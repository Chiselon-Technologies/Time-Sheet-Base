package com.timesheet.employee.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.timesheet.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByEmailIdAndPassword(String emailId, String password);

    List<Employee> findByFirstName(String firstName);

    Optional<Employee> findByEmailId(String emailId);

    Optional<Employee> findByMobileNumber(Long mobileNumber);

    Optional<Employee> findByAadharNumber(Long aadharNumber);

    Optional<Employee> findByPanNumber(String panNumber);
}
