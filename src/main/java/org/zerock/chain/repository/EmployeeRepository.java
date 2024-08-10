package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // 커스텀 쿼리 메소드들
}
