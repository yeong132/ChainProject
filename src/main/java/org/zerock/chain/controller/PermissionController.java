package org.zerock.chain.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.chain.dto.PermissionDTO;
import org.zerock.chain.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;


    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByEmployeeId(@PathVariable Long id) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByEmployeeId(id);
        return ResponseEntity.ok(permissions);
    }
}