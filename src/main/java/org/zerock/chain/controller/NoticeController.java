package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
@Log4j2
public class NoticeController {


    @GetMapping("/list")
    public String noticeList(Model model) {
        return "notice/list";
    }

    // 공지사항  문서 상세  확인 페이지
    @GetMapping("/detail")
    public String noticeDetail(Model model) {
        return "notice/detail";
    }

    // 공지사항  문서수정 페이지
    @GetMapping("/modify")
    public String noticeModify(Model model) {
        return "notice/modify";
    }


    // 공지사항 문서 생성 페이지
    @GetMapping("/register")
    public String noticeRegister(Model model) {
        return "notice/register";
    }


}
