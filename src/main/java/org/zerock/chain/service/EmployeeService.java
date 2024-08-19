package org.zerock.chain.service;

import org.springframework.data.domain.Page;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.dto.PermissionDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(Long empNo);
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO updateEmployee(Long empNo, EmployeeDTO employeeDTO);
    void deleteEmployee(Long empNo);
    List<EmployeeDTO> searchEmployees(String name, String departmentName, String rankName);

    Page<EmployeeDTO> getEmployeesPaged(int page, int size);
    List<EmployeeDTO> getEmployeesByDepartmentId(Long departmentId);
//
//
    void updateEmployeePermissions(Long empNo, List<Long> permissionIds);
    List<PermissionDTO> getEmployeePermissions(Long empNo);
}