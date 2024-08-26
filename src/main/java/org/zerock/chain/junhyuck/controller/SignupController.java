package org.zerock.chain.junhyuck.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zerock.chain.junhyuck.model.Signup;
import org.zerock.chain.junhyuck.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.zerock.chain.pse.model.CustomUserDetails;

import java.time.LocalDate;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입 폼을 보여주는 GET 요청 핸들러
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signup", new Signup());
        return "signup";
    }

    // 회원가입 처리 POST 요청 핸들러
    @PostMapping("/signup")
    public String registerSignup(@ModelAttribute Signup signup) {
        // 입력된 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(signup.getPassword());
        signup.setPassword(encodedPassword);

        // 현재 날짜를 고용일자로 설정
        signup.setHireDate(LocalDate.now());

        // 새로운 회원 정보를 데이터베이스에 저장
        signupRepository.save(signup);

        // 회원가입이 완료되면 로그인 페이지로 리다이렉트
        return "redirect:/login";
    }

    // 로그인 페이지를 보여주는 GET 요청 핸들러
    @GetMapping("/login")
    public String loginPage() {
        // 현재 인증된 사용자가 있는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            // 이미 로그인된 상태라면 메인 페이지로 리다이렉트
            return "redirect:/";
        }
        // 로그인 페이지로 이동
        return "login";
    }

    // 메인 페이지를 보여주는 GET 요청 핸들러
    @GetMapping("/")
    public String home(Authentication authentication, HttpSession session, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long empNo = userDetails.getEmpNo();

            Signup user = signupRepository.findByEmpNo(empNo)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // fullName을 가져와서 모델에 추가
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("empNo", user.getEmpNo());
        }
        return "index"; // index.html 템플릿으로 이동
    }


}
