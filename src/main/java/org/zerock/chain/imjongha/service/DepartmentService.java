package org.zerock.chain.imjongha.service;

import org.zerock.chain.imjongha.model.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department createDepartment(Department department);
    Department updateDepartment(Long id, Department department);
    void deleteDepartment(Long id);
}