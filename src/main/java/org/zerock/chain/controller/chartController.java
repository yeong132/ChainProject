package org.zerock.niceadmin.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chart")
@Log4j2
public class chartController {

    //  차트 페이지
    @GetMapping("/main")
    public String modify(Model model) {
        return "chart/main";
    }


    //  OKR 차트 페이지
    @GetMapping("/OKR")
    public String okrCha(Model model) {
        return "chart/OKR";
    }

    //  프로젝트 차트 페이지
    @GetMapping("/project")
    public String projectCha(Model model) {
        return "chart/project";
    }


    //  OKR 차트 생성 페이지
    @GetMapping("/okrRegister")
    public String okrRegister(Model model) {
        return "chart/okrRegister";
    }

    //  프로젝트 차트 생성 페이지
    @GetMapping("/projectRegister")
    public String projectChaRegister(Model model) {
        return "chart/projectRegister";
    }

}
