package org.zerock.chain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.EmpRankMappingDTO;
import org.zerock.chain.service.EmpRankMappingService;

import java.util.List;

@RestController
@RequestMapping("/empRankMappings")
public class EmpRankMappingController {

    @Autowired
    private EmpRankMappingService empRankMappingService;

    @GetMapping
    public List<EmpRankMappingDTO> getAllEmpRankMappings() {
        return empRankMappingService.getAllEmpRankMappings();
    }

    @GetMapping("/{id}")
    public EmpRankMappingDTO getEmpRankMappingById(@PathVariable int id) {
        return empRankMappingService.getEmpRankMappingById(id);
    }

    @PostMapping
    public EmpRankMappingDTO createEmpRankMapping(@RequestBody EmpRankMappingDTO empRankMappingDTO) {
        return empRankMappingService.createEmpRankMapping(empRankMappingDTO);
    }

    @PutMapping("/{id}")
    public EmpRankMappingDTO updateEmpRankMapping(@PathVariable int id, @RequestBody EmpRankMappingDTO empRankMappingDTO) {
        return empRankMappingService.updateEmpRankMapping(id, empRankMappingDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEmpRankMapping(@PathVariable int id) {
        empRankMappingService.deleteEmpRankMapping(id);
    }
}
