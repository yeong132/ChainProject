package org.zerock.chain.service;

import org.springframework.stereotype.Service;
import org.zerock.chain.model.Employee;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.repository.EmployeeRepository;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeDTO getEmployeeById(Long empNo) {
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        // DTO 변환 및 반환 로직
        return mapToEmployeeDTO(employee);
    }

    private EmployeeDTO mapToEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        // Mapping logic...
        return dto;
    }
}
