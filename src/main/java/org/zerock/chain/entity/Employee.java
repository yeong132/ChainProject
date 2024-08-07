package org.zerock.chain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
//import org.springframework.data.annotation.Id;

@Setter
@Getter
@Entity // 이 클래스가 JPA 엔티티임을 나타냅니다.
@Table(name = "employees") // 데이터베이스의 'employees' 테이블과 매핑됩니다.
public class Employee {

    // Getters and Setters
    // Getters and Setters
    @Id // 이 필드가 기본 키임을 나타냅니다.
    private int emp_no; // 사원 번호 필드

    private String first_name; // 사원의 이름 필드
    private String last_name; // 사원의 성 필드
    private String phone_num; // 사원의 전화번호 필드
    @Column(name = "birth_date")
    private Date birth_date; // 생일 필드, 데이터 유형을 Date로 설정
    private String addr; // 사원의 주소 필드
    private String email; // 사원의 이메일 필드
    @Column(name = "hire_date")
    private Date hire_date; // 입사일 필드, 데이터 유형을 Date로 설정

    @Column(name = "last_date")
    private Date last_date; // 퇴사일 필드, 데이터 유형을 Date로 설정


    //    데이터 처리 과정
//필드 정의: 사원의 각 정보를 나타내는 필드를 정의합니다.
//기본 키 설정: @Id 어노테이션을 사용하여 emp_no 필드를 기본 키로 설정합니다.
//Getter와 Setter: 각 필드에 대한 접근자 메서드(getter)와 설정자 메서드(setter)를 정의합니다.
//요약 및 핵심
//Employee 엔티티는 사원 정보를 나타내며, 데이터베이스의 employee 테이블과 매핑됩니다.
//emp_no 필드는 기본 키로 설정됩니다.
//이 클래스는 사원의 각 정보를 저장하고 접근하는 데 사용됩니다.

}