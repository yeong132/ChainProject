package org.zerock.chain.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendance")
@Log4j2
public class AttendanceController {

    //근퇴 메인 페이지
    @GetMapping("/company")
    public String atDepartment() {return "admin/attendance/company";}

    // 근태 상세 페이지(사원 근태 페이지)
    @GetMapping("/employee")
    public String atEmployee(){ return "admin/attendance/employee";}



}


