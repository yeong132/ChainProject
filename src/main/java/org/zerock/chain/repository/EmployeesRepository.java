package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.domain.EmployeesEntity;
import org.zerock.chain.model.Employee;

public interface EmployeesRepository extends JpaRepository<Employee, Long> {
}
