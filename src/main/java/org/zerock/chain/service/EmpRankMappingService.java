package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.EmpRankMappingDTO;
import org.zerock.chain.entity.EmpRankMapping;
import org.zerock.chain.repository.EmpRankMappingRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpRankMappingService {

    @Autowired
    private EmpRankMappingRepository empRankMappingRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 모든 사원-직급 매핑 목록을 조회하여 DTO 리스트로 반환합니다.
    public List<EmpRankMappingDTO> getAllEmpRankMappings() {
        List<EmpRankMapping> empRankMappings = empRankMappingRepository.findAll();
        return empRankMappings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 주어진 매핑 번호로 사원-직급 매핑을 조회하여 DTO로 반환합니다.
    public EmpRankMappingDTO getEmpRankMappingById(int id) {
        Optional<EmpRankMapping> empRankMapping = empRankMappingRepository.findById(id);
        return empRankMapping.map(this::convertToDTO).orElse(null);
    }

    // 새로운 사원-직급 매핑을 생성합니다.
    public EmpRankMappingDTO createEmpRankMapping(EmpRankMappingDTO empRankMappingDTO) {
        EmpRankMapping empRankMapping = convertToEntity(empRankMappingDTO);
        empRankMappingRepository.save(empRankMapping);
        return convertToDTO(empRankMapping);
    }

    // 기존 사원-직급 매핑 정보를 업데이트합니다.
    public EmpRankMappingDTO updateEmpRankMapping(int id, EmpRankMappingDTO empRankMappingDTO) {
        Optional<EmpRankMapping> existingEmpRankMappingOptional = empRankMappingRepository.findById(id);
        if (existingEmpRankMappingOptional.isPresent()) {
            EmpRankMapping existingEmpRankMapping = existingEmpRankMappingOptional.get();
            modelMapper.map(empRankMappingDTO, existingEmpRankMapping); // DTO의 값으로 엔티티를 업데이트
            empRankMappingRepository.save(existingEmpRankMapping);
            return convertToDTO(existingEmpRankMapping);
        } else {
            return null; // 존재하지 않는 경우
        }
    }

    // 주어진 매핑 번호로 사원-직급 매핑을 삭제합니다.
    public void deleteEmpRankMapping(int id) {
        empRankMappingRepository.deleteById(id);
    }

    // EmpRankMapping 엔티티를 EmpRankMappingDTO로 변환합니다.
    private EmpRankMappingDTO convertToDTO(EmpRankMapping empRankMapping) {
        return modelMapper.map(empRankMapping, EmpRankMappingDTO.class);
    }

    // EmpRankMappingDTO를 EmpRankMapping 엔티티로 변환합니다.
    private EmpRankMapping convertToEntity(EmpRankMappingDTO empRankMappingDTO) {
        return modelMapper.map(empRankMappingDTO, EmpRankMapping.class);
    }
}
