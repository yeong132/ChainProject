package org.zerock.niceadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LinkController {

    // 실제 사이트를 만들 때 필요한 것들

    // example.html
    /*@GetMapping("/")
    public String home() {
        return "example"; // templates 폴더의 example.html 파일을 반환
    }*/

    // 참고용 사이트(사이트 완전 제작 후 삭제)

    // Components-alerts
    @GetMapping("/components-alerts")
    public String getComponentsAlerts() {
        return "components-alerts"; // templates 폴더의 components-alerts.html 파일을 반환
    }

    @GetMapping("/components-accordion")
    public String getComponentsAccordion() {
        return "components-accordion"; // templates 폴더의 components-accordian.html 파일을 반환
    }
}

