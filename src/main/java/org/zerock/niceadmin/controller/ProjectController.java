package org.zerock.niceadmin.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
@Log4j2
public class ProjectController {

    @GetMapping("/list")
    public String list(Model model) {
        // 필요한 데이터 처리
        return "project/list"; // "project/list.html"을 반환
    }

    // 프로젝트 개별 상세  확인 페이지
    @GetMapping("/detail")
    public String detail(Model model) {
        return "project/detail";
    }

    //  임시보관 프로젝트 개별 상세  확인 페이지
    @GetMapping("/임시보관detail")
    public String detail2(Model model) {
        return "project/임시보관detail";
    }

    // 프로젝트 수정 페이지
    @GetMapping("/modify")
    public String modify(Model model) {
        return "project/modify";
    }


    // 새 프로젝트 생성 페이지
    @GetMapping("/register")
    public String register(Model model) {
        return "project/register";
    }

    //  프로젝트 차트 페이지
    @GetMapping("/char")
    public String charPage(Model model) {
        return "project/char";
    }

}
