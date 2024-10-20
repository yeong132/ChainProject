package org.zerock.chain.parkyeongmin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity(name = "EmployeeParkyeongmin")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_no")
    private Long empNo;              // 사원번호

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;        // 이름

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;         // 성

    @Column(name = "phone_num", nullable = false, length = 15)
    private String phoneNum;         // 전화번호

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;     // 생년월일

    @Column(name = "addr", length = 255)
    private String addr;             // 주소

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;            // 이메일

    @Column(name = "hire_date")
    private LocalDate hireDate;      // 고용일

    @Column(name = "last_date")
    private LocalDate lastDate;      // 퇴사일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmp_no")
    private Department department;   // HTML에서 employee.department.dmp_no로 접근
                                     // 여러 직원(Employee)이 특정한 부서(Department)와 연결됨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_no")
    private Rank rank;               // HTML에서 employee.department.rank_no로 접근
                                     // 여러 직원(Employee)이 특정한 직급(rank)과 연결됨
}