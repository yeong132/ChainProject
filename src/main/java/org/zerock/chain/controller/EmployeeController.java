package org.zerock.chain.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<org.zerock.chain.dto.EmployeeDTO>> getAllEmployees() {
        List<org.zerock.chain.dto.EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<org.zerock.chain.dto.EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        org.zerock.chain.dto.EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<org.zerock.chain.dto.EmployeeDTO> createEmployee(@RequestBody org.zerock.chain.dto.EmployeeDTO employeeDTO) {
        org.zerock.chain.dto.EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<org.zerock.chain.dto.EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody org.zerock.chain.dto.EmployeeDTO employeeDTO) {
        org.zerock.chain.dto.EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<org.zerock.chain.dto.EmployeeDTO>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String rankName) {
        List<org.zerock.chain.dto.EmployeeDTO> employees = employeeService.searchEmployees(name, departmentName, rankName);
        return ResponseEntity.ok(employees);
    }

    @GetMapping
    public ResponseEntity<Page<org.zerock.chain.dto.EmployeeDTO>> getEmployeesPaged(
            @RequestParam int page,
            @RequestParam int size) {
        Page<org.zerock.chain.dto.EmployeeDTO> employeePage = employeeService.getEmployeesPaged(page, size);
        return ResponseEntity.ok(employeePage);
    }
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<org.zerock.chain.dto.EmployeeDTO>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<org.zerock.chain.dto.EmployeeDTO> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<org.zerock.chain.dto.PermissionDTO>> getEmployeePermissions(@PathVariable Long id) {
        List<org.zerock.chain.dto.PermissionDTO> permissions = employeeService.getEmployeePermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<Void> updateEmployeePermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        employeeService.updateEmployeePermissions(id, permissionIds);
        return ResponseEntity.noContent().build();
    }
}