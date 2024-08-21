package org.zerock.chain.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.zerock.chain.model.Signup;
import org.zerock.chain.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder; // 암호화에 사용할 PasswordEncoder 빈 주입

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signup", new Signup());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerSignup(@ModelAttribute Signup signup) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signup.getPassword());
        signup.setPassword(encodedPassword);

        // hire_date를 오늘 날짜로 설정
        signup.setHireDate(LocalDate.now());

        // 데이터베이스에 회원 정보 저장
        signupRepository.save(signup);

        // 회원가입 완료 후 login 페이지로 리다이렉트
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/"; // 이미 로그인된 상태라면 메인 페이지로 리다이렉트
        }
        return "login"; // 그렇지 않다면 로그인 페이지로 이동
    }

    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 리다이렉트
        }

        // UserDetails에서 사용자 성과 이름 가져오기
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String fullName = userDetails.getUsername();  // 성과 이름을 fullName으로 가져옴
            model.addAttribute("fullName", fullName);
        }

        return "index"; // 인증된 경우 홈 페이지로 이동
    }
}