package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}