package org.zerock.chain.repository;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT new org.zerock.chain.dto.EmployeeDTO(e.empNo, e.lastName, e.firstName, e.phoneNum, e.profileImg, e.hireDate, e.lastDate, e.birthDate, e.addr, r.rankName, d.dmpName) " +
            "FROM Employee e " +
            "JOIN e.rank r " +
            "JOIN e.department d " +
            "ORDER BY d.dmpName, r.rankName")
    List<EmployeeDTO> findOrganization();
}

