package org.zerock.chain.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.dto.PermissionDTO;
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
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String departmentName,
            @RequestParam(required = false) String rankName) {
        List<EmployeeDTO> employees = employeeService.searchEmployees(name, departmentName, rankName);
        return ResponseEntity.ok(employees);
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getEmployeesPaged(
            @RequestParam int page,
            @RequestParam int size) {
        Page<EmployeeDTO> employeePage = employeeService.getEmployeesPaged(page, size);
        return ResponseEntity.ok(employeePage);
    }
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<PermissionDTO>> getEmployeePermissions(@PathVariable Long id) {
        List<PermissionDTO> permissions = employeeService.getEmployeePermissions(id);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<Void> updateEmployeePermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        employeeService.updateEmployeePermissions(id, permissionIds);
        return ResponseEntity.noContent().build();
    }
}