package com.create.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.create.project.entity.Supervisor;

public interface SupervisorRepository extends JpaRepository<Supervisor, String>{

}
