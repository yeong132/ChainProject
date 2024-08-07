package org.zerock.chain.service;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.EmpDmpMappingDTO;
import org.zerock.chain.entity.EmpDmpMapping;
import org.zerock.chain.repository.EmpDmpMappingRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EmpDmpMappingService {

    @Autowired
    private EmpDmpMappingRepository empDmpMappingRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 모든 사원-부서 매핑 목록을 조회하여 DTO 리스트로 반환합니다.
    public List<EmpDmpMappingDTO> getAllEmpDmpMappings() {
        List<EmpDmpMapping> empDmpMappings = empDmpMappingRepository.findAll();
        return empDmpMappings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 주어진 매핑 번호로 사원-부서 매핑을 조회하여 DTO로 반환합니다.
    public EmpDmpMappingDTO getEmpDmpMappingById(int id) {
        Optional<EmpDmpMapping> empDmpMapping = empDmpMappingRepository.findById(id);
        return empDmpMapping.map(this::convertToDTO).orElse(null);
    }

    // 새로운 사원-부서 매핑을 생성합니다.
    public EmpDmpMappingDTO createEmpDmpMapping(EmpDmpMappingDTO empDmpMappingDTO) {
        EmpDmpMapping empDmpMapping = convertToEntity(empDmpMappingDTO);
        empDmpMappingRepository.save(empDmpMapping);
        return convertToDTO(empDmpMapping);
    }

    // 기존 사원-부서 매핑 정보를 업데이트합니다.
    public EmpDmpMappingDTO updateEmpDmpMapping(int id, EmpDmpMappingDTO empDmpMappingDTO) {
        Optional<EmpDmpMapping> existingEmpDmpMappingOptional = empDmpMappingRepository.findById(id);
        if (existingEmpDmpMappingOptional.isPresent()) {
            EmpDmpMapping existingEmpDmpMapping = existingEmpDmpMappingOptional.get();
            modelMapper.map(empDmpMappingDTO, existingEmpDmpMapping); // DTO의 값으로 엔티티를 업데이트
            empDmpMappingRepository.save(existingEmpDmpMapping);
            return convertToDTO(existingEmpDmpMapping);
        } else {
            return null; // 존재하지 않는 경우
        }
    }

    // 주어진 매핑 번호로 사원-부서 매핑을 삭제합니다.
    public void deleteEmpDmpMapping(int id) {
        empDmpMappingRepository.deleteById(id);
    }

    // EmpDmpMapping 엔티티를 EmpDmpMappingDTO로 변환합니다.
    private EmpDmpMappingDTO convertToDTO(EmpDmpMapping empDmpMapping) {
        return modelMapper.map(empDmpMapping, EmpDmpMappingDTO.class);
    }

    // EmpDmpMappingDTO를 EmpDmpMapping 엔티티로 변환합니다.
    private EmpDmpMapping convertToEntity(EmpDmpMappingDTO empDmpMappingDTO) {
        return modelMapper.map(empDmpMappingDTO, EmpDmpMapping.class);
    }
}