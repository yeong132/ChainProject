package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDTO {
    private Long empNo; // 사원 번호
    private String lastName; // 성
    private String firstName; // 이름
    private String phoneNum; // 휴대번호
//    private String email; // 없앨 예정
    private String profileImg; // 프로필 사진
    private LocalDate hireDate; // 입사일
    private LocalDate lastDate; // 퇴사일
    private LocalDate birthDate; // 생일
    private String addr; // 주소
    private String rankName;  // 직급명
    private String dmpName;   // 부서명
}
