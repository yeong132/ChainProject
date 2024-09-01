package org.zerock.chain.imjongha.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.imjongha.dto.EmployeeDTO;
import org.zerock.chain.imjongha.dto.PermissionDTO;
import org.zerock.chain.imjongha.service.EmployeeService;
import org.zerock.chain.pse.model.CustomUserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    private CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) principal;
            }
        }
        return null;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable("id") Long id) {  // "id" 명시적으로 지정
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeDTO employeeDTO) {  // "id" 명시적으로 지정
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {  // "id" 명시적으로 지정
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // 박성은 추가 코드
    @GetMapping("/employee-info")
    public ResponseEntity<Map<String, String>> employeeInfo(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            response.put("username", authentication.getName()); // 사원이름

            // 세션에서 사원번호 가져오기
            Object empNo = request.getSession().getAttribute("empNo");
            if (empNo != null) {
                response.put("empNo", empNo.toString());
            } else {
                response.put("empNo", "N/A");
            }
        } else {
            response.put("empNo", "N/A");
            response.put("username", "N/A");
        }

        return ResponseEntity.ok(response);
    }

    // 로그인한 사원번호를 제외한 전체 사원 목록 조회
    @GetMapping("/all-except-logged-in")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployeesExceptLoggedIn() {
        CustomUserDetails loggedInUser = getAuthenticatedUser();
        Long loggedInEmpNo = loggedInUser != null ? loggedInUser.getEmpNo() : null;

        List<EmployeeDTO> employees = employeeService.getAllEmployeesExcept(loggedInEmpNo);
        return ResponseEntity.ok(employees);
    }
}
