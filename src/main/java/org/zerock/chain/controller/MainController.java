package org.zerock.chain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html 파일을 서빙
    }

    @GetMapping("/example")
    public String example() {
        return "example"; // templates/example.html 파일을 서빙
    }

    @GetMapping("/chatting")
    public String chatMain() {
        return "chatting"; // templates/chatting.html 파일을 서빙
    }

    @GetMapping("/organization_chart")
    public String organization_chart() {
        return "organization_chart"; // templates/organization_chart.html 파일을 서빙
    }
}
