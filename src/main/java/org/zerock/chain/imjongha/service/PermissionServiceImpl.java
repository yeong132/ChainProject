package org.zerock.chain.imjongha.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.imjongha.dto.PermissionDTO;
import org.zerock.chain.imjongha.exception.EmployeeNotFoundException;
import org.zerock.chain.imjongha.model.Employee;
import org.zerock.chain.imjongha.model.EmployeePermission;
import org.zerock.chain.imjongha.model.Permission;
import org.zerock.chain.imjongha.repository.EmployeePermissionRepository;
import org.zerock.chain.imjongha.repository.EmployeeRepository;
import org.zerock.chain.imjongha.repository.PermissionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final EmployeeRepository employeeRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final ModelMapper modelMapper;

    public PermissionServiceImpl(EmployeeRepository employeeRepository,
                                 PermissionRepository permissionRepository,
                                 EmployeePermissionRepository employeePermissionRepository,
                                 ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.permissionRepository = permissionRepository;
        this.employeePermissionRepository = employeePermissionRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionDTO> getPermissionsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("사원을 찾을 수 없습니다. ID: " + employeeId));
        return employee.getEmployeePermissions().stream()
                .map(ep -> modelMapper.map(ep.getPermission(), PermissionDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateEmployeePermissions(Long empNo, List<Long> permissionIds) {
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new EmployeeNotFoundException("사원을 찾을 수 없습니다. ID: " + empNo));

        // 현재 권한 목록을 가져옴
        List<Long> existingPermissionIds = employee.getEmployeePermissions().stream()
                .map(ep -> ep.getPermission().getPerNo())
                .collect(Collectors.toList());

        // 추가할 권한 결정
        List<Long> permissionsToAdd = permissionIds.stream()
                .filter(permissionId -> !existingPermissionIds.contains(permissionId))
                .collect(Collectors.toList());

        // 제거할 권한 결정
        List<EmployeePermission> permissionsToRemove = employee.getEmployeePermissions().stream()
                .filter(ep -> !permissionIds.contains(ep.getPermission().getPerNo()))
                .collect(Collectors.toList());

        // 기존 권한 삭제
        employeePermissionRepository.deleteAll(permissionsToRemove);

        // 새로운 권한 추가
        List<Permission> permissions = permissionRepository.findAllById(permissionsToAdd);
        permissions.forEach(permission -> {
            EmployeePermission employeePermission = new EmployeePermission(employee, permission);
            employeePermissionRepository.save(employeePermission);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionDTO> getEmployeePermissions(Long empNo) {
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new EmployeeNotFoundException("사원을 찾을 수 없습니다. ID: " + empNo));
        return employee.getEmployeePermissions().stream()
                .map(ep -> modelMapper.map(ep.getPermission(), PermissionDTO.class))
                .collect(Collectors.toList());
    }

    private PermissionDTO convertToDTO(Permission permission) {
        return modelMapper.map(permission, PermissionDTO.class);
    }

    private Permission convertToEntity(PermissionDTO dto) {
        return modelMapper.map(dto, Permission.class);
    }
}
