package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.DepartmentDTO;
import org.zerock.chain.entity.Department;
import org.zerock.chain.repository.DepartmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 모든 부서 목록을 조회하여 DTO 리스트로 반환합니다.
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // 새로운 부서를 생성합니다.
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Department department = convertToEntity(departmentDTO);
        departmentRepository.save(department);
        return convertToDTO(department);
    }

    // 기존 부서 정보를 업데이트합니다.
    public DepartmentDTO updateDepartment(int dmpNo, DepartmentDTO departmentDTO) {
        Optional<Department> existingDepartmentOptional = departmentRepository.findById(dmpNo);
        if (existingDepartmentOptional.isPresent()) {
            Department existingDepartment = existingDepartmentOptional.get();
            modelMapper.map(departmentDTO, existingDepartment); // DTO의 값으로 엔티티를 업데이트
            departmentRepository.save(existingDepartment);
            return convertToDTO(existingDepartment);
        } else {
            return null; // 존재하지 않는 경우
        }
    }

    // 주어진 부서 번호로 부서를 삭제합니다.
    public void deleteDepartment(int dmpNo) {
        departmentRepository.deleteById(dmpNo);
    }

    // Department 엔티티를 DepartmentDTO로 변환합니다.
    private DepartmentDTO convertToDTO(Department department) {
        return modelMapper.map(department, DepartmentDTO.class);
    }

    // DepartmentDTO를 Department 엔티티로 변환합니다.
    private Department convertToEntity(DepartmentDTO departmentDTO) {
        return modelMapper.map(departmentDTO, Department.class);
    }
}
