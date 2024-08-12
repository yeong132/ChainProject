package org.zerock.chain.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.model.Employee;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.repository.EmployeeRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

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

    public Map<String, List<EmployeeDTO>> getOrganization() {
        List<EmployeeDTO> employees = employeeRepository.findOrganization();

        return employees.stream().collect(Collectors.groupingBy(EmployeeDTO::getDmpName));
    }

}
