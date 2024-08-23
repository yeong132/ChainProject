package org.zerock.chain.imjongha.service;


import org.springframework.stereotype.Service;
import org.zerock.chain.imjongha.dto.PermissionDTO;

import java.util.List;

@Service
public interface PermissionService {
    List<PermissionDTO> getAllPermissions();
    List<PermissionDTO> getPermissionsByEmployeeId(Long employeeId);
}