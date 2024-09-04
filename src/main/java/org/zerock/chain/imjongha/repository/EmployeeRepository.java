package org.zerock.chain.imjongha.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.imjongha.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {
    @EntityGraph(attributePaths = {"department", "rank"})
    List<Employee> findByDepartmentDmpNo(Long departmentId);

    @EntityGraph(attributePaths = {"department", "rank", "employeePermissions.permission"})
    Optional<Employee> findById(Long id);

    // 박성은 추가 코드
    List<Employee> findAllByEmpNoNot(Long empNo);

    // 부서,직급,연차 정보 들고오는 메서드(영민 추가)
    @EntityGraph(attributePaths = {"department", "rank", "employeeLeave"})
    List<Employee> findAll();
}