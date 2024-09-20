package com.timesheet.supervisor.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.timesheet.supervisor.entity.Supervisor;

public interface SupervisorRepository extends JpaRepository<Supervisor, String> {

	Optional<Supervisor> findByEmailIdAndPassword(String emailId, String password);
}
