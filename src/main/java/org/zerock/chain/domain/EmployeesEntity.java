package org.zerock.chain.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "last_date")
    private LocalDate lastDate;

    private String email;

    @Column(name = "phone_num")
    private String phoneNum;

    @Column(name = "profile_img")
    private String profileImg;
}
