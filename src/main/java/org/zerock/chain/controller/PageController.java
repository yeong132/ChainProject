package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class PageController {
    @GetMapping("/chatting")
    public String chatMain() {
        return "chatting"; // chatting.html 파일을 템플릿 엔진이 처리
    }
}