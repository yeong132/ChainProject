package org.zerock.chain.parkyeongmin.dto;


import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long empNo;            // 사원 번호
    private String firstName;      // 이름
    private String lastName;       // 성
    private String phoneNum;       // 전화번호
    private LocalDate birthDate;   // 생년월일
    private String addr;           // 주소
    private String email;          // 이메일
    private LocalDate hireDate;    // 고용일
    private LocalDate lastDate;    // 퇴사일
    private Long dmpNo;            // 부서 번호
    private Long rankNo;           // 직급 번호
    private String dmpName;        // 부서 이름
    private String rankName;       // 직급 이름
}