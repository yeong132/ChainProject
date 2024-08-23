package org.zerock.chain.parkyeongmin.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.parkyeongmin.dto.EmployeeDTO;
import org.zerock.chain.parkyeongmin.model.Employee;
import org.zerock.chain.parkyeongmin.repository.EmployeesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final EmployeesRepository employeesRepository;
    private final ModelMapper modelMapper;

    public UserService(EmployeesRepository employeesRepository, ModelMapper modelMapper) {
        this.employeesRepository = employeesRepository;
        this.modelMapper = modelMapper;
    }

    public EmployeeDTO getLoggedInUserDetails() {
        // 임시로 emp_no가 1인 사용자를 가져옴
        Employee employee = employeesRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 엔티티를 DTO로 변환
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmpNo(employee.getEmpNo());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setDmpNo(employee.getDepartment().getDmpNo());
        employeeDTO.setRankNo(employee.getRank().getRankNo());
        // 부서 이름과 직급 이름을 설정
        employeeDTO.setDmpName(employee.getDepartment().getDmpName());
        employeeDTO.setRankName(employee.getRank().getRankName());

        return employeeDTO;
    }

    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeesRepository.findAll();
        return employees.stream()
                .map(employee -> {
                    EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);

                    // rankName과 dmpName을 설정
                    employeeDTO.setRankName(employee.getRank().getRankName());
                    employeeDTO.setDmpName(employee.getDepartment().getDmpName());

                    return employeeDTO;
                })
                .collect(Collectors.toList());
    }
}
