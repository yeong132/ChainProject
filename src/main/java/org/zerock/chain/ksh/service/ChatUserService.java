package org.zerock.chain.ksh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.chain.imjongha.dto.EmployeeDTO;
import org.zerock.chain.ksh.repository.ChatRoomRepository;
import org.zerock.chain.imjongha.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatUserService {
    private final ChatRoomRepository chatRoomRepository;
    private final EmployeeRepository employeeRepository;

    public void saveEmployee(Long empNo) { // 사용자 연결시
////        employee.setStatus(Status.ONLINE); // 온라인 상태 설정
//        employeeRepository.save(employee); // 저장
    }

    public void disconnect(Long empNo) { // 사용자 연결 끊을 시
//        var storedEmployee = employeeRepository.findById(empNo).orElse(null); // 유저id 확인
//        if (storedEmployee != null) { // 저장된 사용자가 null이 아닌 경우
//            storedEmployee.setStatus(Status.OFFLINE); // 상태를 오프라인으로 설정
//            employeeRepository.save(storedEmployee); // 저장
//        }
    }

    // 사원 번호로 사원 정보를 조회하는 메서드
    public EmployeeDTO findEmployeeByEmpNo(Long empNo) {
        return employeeRepository.findById(empNo)
                .map(employee -> EmployeeDTO.builder()
                        .empNo(employee.getEmpNo())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .rankName(employee.getRank().getRankName()) // 직급 이름 가져오기
                        .build())
                .orElse(null); // 사원 번호에 해당하는 사원이 없을 경우 null 반환
    }

    // 대화 중인 사용자 목록 반환
    public List<EmployeeDTO> findActiveChatUsers(Long empNo) {
//        return chatRoomRepository.findActiveChatUsersByEmpNo(empNo);
        // Employee 리스트에서 empNo만 추출하여 리스트로 반환
        return chatRoomRepository.findActiveChatUsersByEmpNo(empNo)
                .stream()
                .map(employee -> EmployeeDTO.builder()
                        .empNo(employee.getEmpNo())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .rankName(employee.getRank().getRankName()) // Department의 이름 가져오기
                        .build())
                .collect(Collectors.toList());
    }
}

