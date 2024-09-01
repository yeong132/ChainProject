package org.zerock.chain.imjongha.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.imjongha.dto.PermissionDTO;
import org.zerock.chain.imjongha.exception.EmployeeNotFoundException;
import org.zerock.chain.imjongha.model.Employee;
import org.zerock.chain.imjongha.model.EmployeePermission;
import org.zerock.chain.imjongha.model.Permission;
import org.zerock.chain.imjongha.repository.EmployeePermissionRepository;
import org.zerock.chain.imjongha.repository.EmployeeRepository;
import org.zerock.chain.imjongha.repository.PermissionRepository;
import org.zerock.chain.imjongha.service.PermissionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final PermissionRepository permissionRepository;
  private final EmployeeRepository employeeRepository;


    public PermissionController(PermissionService permissionService, EmployeePermissionRepository employeePermissionRepository, PermissionRepository permissionRepository,EmployeeRepository employeeRepository) {
        this.permissionService = permissionService;
        this.employeePermissionRepository = employeePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/employees/{id}/permissions")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByEmployeeId(@PathVariable("id") Long id) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByEmployeeId(id);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/employees/{id}/permissions")
    public ResponseEntity<Void> updatePermissionsByEmployeeId(
            @PathVariable("id") Long id,
            @RequestBody List<Long> permissionIds) {

        permissionService.updateEmployeePermissions(id, permissionIds);
        return ResponseEntity.noContent().build();
    }




}