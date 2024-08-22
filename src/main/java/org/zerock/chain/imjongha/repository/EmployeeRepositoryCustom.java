package org.zerock.chain.imjongha.repository;



import org.zerock.chain.imjongha.model.Employee;

import java.util.List;
public interface EmployeeRepositoryCustom {
    List<Employee> findEmployeesByCriteria(String name, String departmentName, String rankName);
}