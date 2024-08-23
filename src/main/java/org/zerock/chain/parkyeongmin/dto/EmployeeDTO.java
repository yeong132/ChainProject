package org.zerock.chain.parkyeongmin.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Long empNo;
    private String firstName;
    private String lastName;
    private String phoneNum;
    private LocalDate birthDate;
    private String addr;
    private String email;
    private LocalDate hireDate;
    private LocalDate lastDate;
    private Long dmpNo;
    private Long rankNo;
    private String dmpName;
    private String rankName;
}