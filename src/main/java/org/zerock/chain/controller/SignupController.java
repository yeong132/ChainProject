package org.zerock.chain.controller;

import org.zerock.chain.model.Signup;
import org.zerock.chain.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signup", new Signup());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerSignup(@ModelAttribute Signup signup) {
        // hire_date를 오늘 날짜로 설정
        signup.setHireDate(LocalDate.now());

        // 데이터베이스에 회원 정보 저장
        signupRepository.save(signup);

        // 회원가입 완료 후 login 페이지로 리다이렉트
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}