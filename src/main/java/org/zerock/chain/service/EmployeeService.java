package org.zerock.chain.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.zerock.chain.dto.EmployeeDTO;

import java.util.List;


public interface EmployeeService {

    //    목록
    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(Long empNo);
    //    생성
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    //    수정
    EmployeeDTO updateEmployee(Long empNo, EmployeeDTO employeeDTO);
    //    삭제
    void deleteEmployee(Long empNo);
    List<EmployeeDTO> searchEmployees(String name, String departmentName, String rankName);
    Page<EmployeeDTO> getEmployeesPaged(int page, int size);
}



