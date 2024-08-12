package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empNo;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "phone_num")
    private String phoneNum;

//    @Column(name = "email")
//    private String email;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "last_date")
    private LocalDate lastDate;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "addr")
    private String addr;

    @ManyToOne
    @JoinColumn(name = "rank_no")
    private EmpRank rank;  // 직급

    @ManyToOne
    @JoinColumn(name = "dmp_no")
    private Department department;  // 부서
}
