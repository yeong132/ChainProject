package org.zerock.chain.parkyeongmin.service;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.parkyeongmin.dto.EmployeeDTO;
import org.zerock.chain.parkyeongmin.model.Employee;
import org.zerock.chain.parkyeongmin.repository.EmployeesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserService {
    private final EmployeesRepository employeesRepository;
    private final ModelMapper modelMapper;

    public UserService(EmployeesRepository employeesRepository, ModelMapper modelMapper) {
        this.employeesRepository = employeesRepository;
        this.modelMapper = modelMapper;
    }

    public EmployeeDTO getLoggedInUserDetails() { // 로그인 기능 구현 시 Long loggedInEmpNo을 ()에 넣기
        try {
            // 임시값으로 사용자를 가져옴               받은 문서함,반려 테스트할때 여기 값 바꾸기
            Employee employee = employeesRepository.findById(4L)  // 로그인 기능 구현 시 임시값 -> loggedInEmpNo으로
                    .orElseThrow(() -> {
                        log.error("User with emp_no 1 not found");
                        return new RuntimeException("User not found");
                    });

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

            log.info("Successfully retrieved logged-in user details for emp_no {}", employee.getEmpNo());

            return employeeDTO;

        } catch (Exception e) {
            log.error("Error occurred while retrieving logged-in user details", e);
            throw e;
        }
    }

    public List<EmployeeDTO> getAllEmployees() {
        try {
            List<Employee> employees = employeesRepository.findAll();
            return employees.stream()
                    .map(employee -> {
                        EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);

                        // rankName과 dmpName을 안전하게 설정
                        if (employee.getRank() != null) {
                            employeeDTO.setRankName(employee.getRank().getRankName());
                        } else {
                            log.warn("Employee with empNo {} has no rank", employee.getEmpNo());
                            employeeDTO.setRankName("Unknown");
                        }

                        if (employee.getDepartment() != null) {
                            employeeDTO.setDmpName(employee.getDepartment().getDmpName());
                        } else {
                            log.warn("Employee with empNo {} has no department", employee.getEmpNo());
                            employeeDTO.setDmpName("Unknown");
                        }

                        return employeeDTO;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error occurred while retrieving all employees", e);
            throw e;
        }
    }
}
