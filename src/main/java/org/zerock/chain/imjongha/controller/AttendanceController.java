package org.zerock.chain.imjongha.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/attendance")
@Log4j2
public class AttendanceController {

    //근퇴 상세 페이지
    @GetMapping("/employee")
    public String atDepartment() {return "admin/attendance/employee";}

    // 근태 전체 페이지
    @GetMapping("/company")
    public String atEmployee(){ return "admin/attendance/company";}




}


