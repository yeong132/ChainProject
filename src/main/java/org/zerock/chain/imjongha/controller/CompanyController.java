package org.zerock.chain.imjongha.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/attendance")
@Log4j2
public class CompanyController {
//
//    // 근태 상세 페이지
//    @GetMapping("/employee")
//    public String atDepartment() {
//        return "admin/attendance/employee";
//    }
//
//    // 근태 전체 페이지
//    @GetMapping("/company")
//    public String atEmployee() {
//        return "/admin/attendance/company";
//    }

    // 휴가 관리 페이지
    @GetMapping("/leave")
    public String atLeave() {
        return "/admin/attendance/leave";
    }

    // 사원 휴가 페이지
    @GetMapping("/emp_leave")
    public String empLeave() {
        return "/admin/attendance/emp_leave";
    }

}
