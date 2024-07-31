package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
@Log4j2
public class BoardController {

    @GetMapping("/식당리스트")
    public String FodList(Model model) {
        return "board/식당리스트";
    }

    @GetMapping("/list")
    public String boardList(Model model) {
        return "board/list";
    }

    // 경조사  문서 상세  확인 페이지
    @GetMapping("/detail")
    public String boardDetail(Model model) {
        return "board/detail";
    }

    // 경조사  문서수정 페이지
    @GetMapping("/modify")
    public String boardModify(Model model) {
        return "board/modify";
    }


    // 경조사 문서 생성 페이지
    @GetMapping("/register")
    public String boardRegister(Model model) {
        return "board/register";
    }


}
