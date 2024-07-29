package org.zerock.niceadmin.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/work")
@Log4j2
public class WorkController {

    @GetMapping("/list")
    public String workList(Model model) {
        return "work/list";
    }

    // 문서 개별 상세  확인 페이지
    @GetMapping("/detail")
    public String workDetail(Model model) {
        return "work/detail";
    }

    // 문서 수정 페이지
    @GetMapping("/modify")
    public String workModify(Model model) {
        return "work/modify";
    }


    // 새 문서 생성 페이지
    @GetMapping("/register")
    public String workRegister(Model model) {
        return "work/register";
    }


}
