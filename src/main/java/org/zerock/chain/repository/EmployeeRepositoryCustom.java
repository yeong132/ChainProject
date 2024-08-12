package org.zerock.chain.repository;



import org.zerock.chain.entity.Employee;

import java.util.List;

public interface EmployeeRepositoryCustom {
    List<Employee> findEmployeesByCriteria(String name, String departmentName, String rankName);
}