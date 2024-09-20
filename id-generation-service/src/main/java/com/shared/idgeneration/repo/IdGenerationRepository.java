package com.shared.idgeneration.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shared.idgeneration.entity.IdGeneration;

@Repository
public interface IdGenerationRepository extends JpaRepository<IdGeneration, Long> {

	 IdGeneration findByIdType(String idType);



}
