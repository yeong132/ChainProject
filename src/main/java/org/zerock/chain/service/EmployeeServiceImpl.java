package org.zerock.chain.service;


import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.dto.PermissionDTO;
import org.zerock.chain.exception.EmployeeNotFoundException;
import org.zerock.chain.model.*;
import org.zerock.chain.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RankRepository rankRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               DepartmentRepository departmentRepository,
                               RankRepository rankRepository,
                               PermissionRepository permissionRepository,
                               EmployeePermissionRepository employeePermissionRepository,
                               ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.rankRepository = rankRepository;
        this.permissionRepository = permissionRepository;
        this.employeePermissionRepository = employeePermissionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(Long empNo) {
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new EntityNotFoundException("사원을 찾을 수 없습니다."));
        return convertToDTO(employee);
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        employee.setHireDate(LocalDate.now());
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Override
    public EmployeeDTO updateEmployee(Long empNo, EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new EntityNotFoundException("사원을 찾을 수 없습니다."));

        updateEmployeeFromDTO(existingEmployee, employeeDTO);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return convertToDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long empNo) {
        employeeRepository.deleteById(empNo);
    }

    @Override
    public List<EmployeeDTO> searchEmployees(String name, String departmentName, String rankName) {
        List<Employee> employees = employeeRepository.findEmployeesByCriteria(name, departmentName, rankName);
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDTO> getEmployeesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);
        return employeePage.map(this::convertToDTO);
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentDmpNo(departmentId);
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateEmployeePermissions(Long empNo, List<Long> permissionIds) {
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new EmployeeNotFoundException("사원을 찾을 수 없습니다."));

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

    @Override
    public List<PermissionDTO> getEmployeePermissions(Long empNo) {
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(() -> new EmployeeNotFoundException("사원을 찾을 수 없습니다."));
        return employee.getEmployeePermissions().stream()
                .map(ep -> modelMapper.map(ep.getPermission(), PermissionDTO.class))
                .collect(Collectors.toList());
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getDmpNo());
            dto.setDepartmentName(employee.getDepartment().getDmpName());
        }
        if (employee.getRank() != null) {
            dto.setRankId(employee.getRank().getRankNo());
            dto.setRankName(employee.getRank().getRankName());
        }
        dto.setPermissionIds(employee.getEmployeePermissions().stream()
                .map(ep -> ep.getPermission().getPerNo())
                .collect(Collectors.toList()));
        return dto;
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = modelMapper.map(dto, Employee.class);

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));
            employee.setDepartment(department);
        }

        if (dto.getRankId() != null) {
            Rank rank = rankRepository.findById(dto.getRankId())
                    .orElseThrow(() -> new EntityNotFoundException("직급을 찾을 수 없습니다."));
            employee.setRank(rank);
        }

        return employee;
    }

    private void updateEmployeeFromDTO(Employee employee, EmployeeDTO employeeDTO) {
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPhoneNum(employeeDTO.getPhoneNum());
        employee.setBirthDate(employeeDTO.getBirthDate());
        employee.setAddr(employeeDTO.getAddr());
        employee.setEmail(employeeDTO.getEmail());
        employee.setLastDate(employeeDTO.getLastDate());

        if (employeeDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));
            employee.setDepartment(department);
        }

        if (employeeDTO.getRankId() != null) {
            Rank rank = rankRepository.findById(employeeDTO.getRankId())
                    .orElseThrow(() -> new EntityNotFoundException("직급을 찾을 수 없습니다."));
            employee.setRank(rank);
        }
    }
}

