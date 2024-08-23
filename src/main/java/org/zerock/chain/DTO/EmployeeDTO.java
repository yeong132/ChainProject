package org.zerock.chain.dto;


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
    private Long departmentId;
    private Long rankId;
    private String departmentName;
    private String rankName;
    private List<Long> permissionIds;


}