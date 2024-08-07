package org.zerock.chain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{empNo}")
    public EmployeeDTO getEmployeeById(@PathVariable int empNo) {
        return employeeService.getEmployeeById(empNo);
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @PutMapping("/{empNo}")
    public EmployeeDTO updateEmployee(@PathVariable int empNo, @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.updateEmployee(empNo, employeeDTO);
    }

    @DeleteMapping("/{empNo}")
    public void deleteEmployee(@PathVariable int empNo) {
        employeeService.deleteEmployee(empNo);
    }
}