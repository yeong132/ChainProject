package org.zerock.chain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.EmpDmpMappingDTO;
import org.zerock.chain.service.EmpDmpMappingService;

import java.util.List;

@RestController
@RequestMapping("/empDmpMappings")
    public class EmpDmpMappingController {

    @Autowired
    private EmpDmpMappingService empDmpMappingService;

    @GetMapping
    public List<EmpDmpMappingDTO> getAllEmpDmpMappings() {
        return empDmpMappingService.getAllEmpDmpMappings();
    }

    @GetMapping("/{id}")
    public EmpDmpMappingDTO getEmpDmpMappingById(@PathVariable int id) {
        return empDmpMappingService.getEmpDmpMappingById(id);
    }

    @PostMapping
    public EmpDmpMappingDTO createEmpDmpMapping(@RequestBody EmpDmpMappingDTO empDmpMappingDTO) {
        return empDmpMappingService.createEmpDmpMapping(empDmpMappingDTO);
    }

    @PutMapping("/{id}")
    public EmpDmpMappingDTO updateEmpDmpMapping(@PathVariable int id, @RequestBody EmpDmpMappingDTO empDmpMappingDTO) {
        return empDmpMappingService.updateEmpDmpMapping(id, empDmpMappingDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEmpDmpMapping(@PathVariable int id) {
        empDmpMappingService.deleteEmpDmpMapping(id);
    }
}
