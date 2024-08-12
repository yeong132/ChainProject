package org.zerock.chain.controller;


import org.zerock.chain.entity.Department;
import org.zerock.chain.dto.DepartmentDTO;
import org.zerock.chain.repository.DepartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// DepartmentController.java
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departmentDTOs = departmentRepository.findAll().stream()
                .map(department -> {
                    DepartmentDTO dto = new DepartmentDTO();
                    dto.setDmpNo(department.getDmpNo());
                    dto.setDmpName(department.getDmpName());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(departmentDTOs);
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        Department department = new Department();
        department.setDmpName(departmentDTO.getDmpName());
        Department savedDepartment = departmentRepository.save(department);

        DepartmentDTO savedDepartmentDTO = new DepartmentDTO();
        savedDepartmentDTO.setDmpNo(savedDepartment.getDmpNo());
        savedDepartmentDTO.setDmpName(savedDepartment.getDmpName());

        return new ResponseEntity<>(savedDepartmentDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        return departmentRepository.findById(id)
                .map(department -> {
                    department.setDmpName(departmentDTO.getDmpName());
                    Department updatedDepartment = departmentRepository.save(department);

                    DepartmentDTO updatedDepartmentDTO = new DepartmentDTO();
                    updatedDepartmentDTO.setDmpNo(updatedDepartment.getDmpNo());
                    updatedDepartmentDTO.setDmpName(updatedDepartment.getDmpName());

                    return ResponseEntity.ok(updatedDepartmentDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}