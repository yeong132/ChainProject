package org.zerock.chain.service;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.entity.Employee;
import org.zerock.chain.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // 이 클래스가 서비스 레이어임을 나타냅니다.
public class EmployeeService {

    @Autowired // 의존성 주입을 위한 어노테이션
    private EmployeeRepository employeeRepository; // EmployeeRepository 인터페이스 주입

    @Autowired
    private ModelMapper modelMapper;

    // 모든 사원 정보를 조회하여 DTO 리스트로 반환하는 메서드
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 사원 번호로 사원 정보를 조회하여 DTO로 반환하는 메서드
    public EmployeeDTO getEmployeeById(int emp_no) {
        // 리포지토리에서 사원 정보를 조회하고, Optional로 반환
        Optional<Employee> employee = employeeRepository.findById(emp_no);
        // 조회된 사원 정보를 DTO로 변환하여 반환
        return employee.map(this::convertToDTO).orElse(null);
    }

    // 사원 DTO를 받아 엔티티로 변환하여 저장하고, 저장된 엔티티를 DTO로 변환하여 반환하는 메서드
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        // DTO를 엔티티로 변환
        Employee employee = convertToEntity(employeeDTO);
        // 엔티티를 리포지토리에 저장
        employeeRepository.save(employee);
        // 저장된 엔티티를 DTO로 변환하여 반환
        return convertToDTO(employee);
    }

    // 기존 사원 정보를 업데이트합니다.
    public EmployeeDTO updateEmployee(int empNo, EmployeeDTO employeeDTO) {
        Optional<Employee> existingEmployeeOptional = employeeRepository.findById(empNo);
        if (existingEmployeeOptional.isPresent()) {
            Employee existingEmployee = existingEmployeeOptional.get();
            modelMapper.map(employeeDTO, existingEmployee); // DTO의 값으로 엔티티를 업데이트
            employeeRepository.save(existingEmployee);
            return convertToDTO(existingEmployee);
        } else {
            return null; // 존재하지 않는 경우
        }
    }


    // 주어진 사원 번호로 사원을 삭제합니다.
    public void deleteEmployee(int empNo) {
        employeeRepository.deleteById(empNo);
    }

    // Employee 엔티티를 EmployeeDTO로 변환합니다.
    private EmployeeDTO convertToDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    // EmployeeDTO를 Employee 엔티티로 변환합니다.
    private Employee convertToEntity(EmployeeDTO employeeDTO) {
        return modelMapper.map(employeeDTO, Employee.class);
    }
}