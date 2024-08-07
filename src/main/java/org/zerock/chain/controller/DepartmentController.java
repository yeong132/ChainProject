package org.zerock.chain.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.DepartmentDTO;
import org.zerock.chain.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public List<DepartmentDTO> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

//    @GetMapping("/{dmpNo}")
//    public DepartmentDTO getDepartmentById(@PathVariable int dmpNo) {
//        return departmentService.getDepartmentById(dmpNo);
//    }

    @PostMapping
    public DepartmentDTO createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        return departmentService.createDepartment(departmentDTO);
    }

    @PutMapping("/{dmpNo}")
    public DepartmentDTO updateDepartment(@PathVariable int dmpNo, @RequestBody DepartmentDTO departmentDTO) {
        return departmentService.updateDepartment(dmpNo, departmentDTO);
    }

    @DeleteMapping("/{dmpNo}")
    public void deleteDepartment(@PathVariable int dmpNo) {
        departmentService.deleteDepartment(dmpNo);
    }
}
