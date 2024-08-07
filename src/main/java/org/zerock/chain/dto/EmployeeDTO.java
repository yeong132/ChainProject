package org.zerock.chain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class EmployeeDTO {

    // Getters and Setters
    private int emp_no; // 사원 번호 필드
    private String first_name; // 사원의 이름 필드
    private String last_name; // 사원의 성 필드
    private String phone_num; // 사원의 전화번호 필드
    private Date birth_date; // 생일 필드, 데이터 유형을 Date로 설정
    private String addr; // 사원의 주소 필드
    private String email; // 사원의 이메일 필드
    private Date hire_date; // 입사일 필드, 데이터 유형을 Date로 설정
    private Date last_date; // 퇴사일 필드, 데이터 유형을 Date로 설정

}
