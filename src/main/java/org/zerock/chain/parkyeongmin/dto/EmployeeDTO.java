package org.zerock.chain.parkyeongmin.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
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