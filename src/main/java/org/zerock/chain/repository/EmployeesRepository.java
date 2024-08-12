package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.domain.EmployeesEntity;

public interface EmployeesRepository extends JpaRepository<EmployeesEntity, Integer> {
}
