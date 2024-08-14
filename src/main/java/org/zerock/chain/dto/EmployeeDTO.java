package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long empNo; // 사원 번호
    private String lastName; // 성
    private String firstName; // 이름
    private String phoneNum; // 휴대번호
    private String profileImg; // 프로필 사진
    private LocalDate hireDate; // 입사일
    private LocalDate lastDate; // 퇴사일
    private LocalDate birthDate; // 생일
    private String addr; // 주소
    private Long departmentNo; // 부서 번호
    private Long rankNo; // 직급 번호
    private String rankName;  // 직급명
    private String dmpName;   // 부서명
}
