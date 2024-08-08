package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    //  마이페이지 페이지
    @GetMapping("/mypage")
    public String userMypage(Model model) {
        return "user/mypage";
    }

    //  환경설정 페이지
    @GetMapping("/setting")
    public String UserSetting(Model model) {
        return "user/setting";
    }



    //  고객센터 페이지 조회 (전체목록조회)
    @GetMapping("/Q&A")
    public String UserQAList(Model model) {
        return "user/Q&A";
    }

    //  고객센터  작성 페이지 (등록)
    @GetMapping("/qaRegister")
    public String UserQARegister(Model model) {
        return "user/qaRegister";
    }

    //  고객센터 상세 조회 페이지
    //  고객센터 상세 조회 페이지 (수정/삭제)
    @GetMapping("/qaDetail")
    public String UserQADetail(Model model) {
        return "user/qaDetail";
    }




    //  로그아웃 페이지
    @GetMapping("/logout")
    public String UserLogout(Model model) {return "user/logout";}

    //  로그인 페이지
    @GetMapping("/login")
    public String UserLogin(Model model) {
        return "user/login";
    }

    //  환경설정 페이지
    @GetMapping("/alarm")
    public String UserAlarm(Model model) {
        return "user/alarm";
    }
}
