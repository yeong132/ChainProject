package org.zerock.chain.parkyeongmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.parkyeongmin.model.Employee;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Long> {

    // 직원 번호(empNo)에 해당하는 사용자의 풀네임(성 + 이름) 조회 - 풀네임을 가져오는 메서드가 있는게 편해서
    @Query("SELECT CONCAT(e.lastName, e.firstName) FROM Employee e WHERE e.empNo = :empNo")
    String findFullNameByEmpNo(@Param("empNo") Long empNo);
}
