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
import org.zerock.chain.pse.dto.ProjectDTO;
import org.zerock.chain.pse.model.CustomUserDetails;
import org.zerock.chain.pse.service.ProjectService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private ProjectService projectService;

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

        // dmp_nos는 0, rank_no는 기본값으로 이미 1이 설정됨
        signup.setDmpNo(0L);
        signup.setRankNo(1L);

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

            // 세션에 로그인 정보를 저장
            session.setAttribute("empNo", empNo);
            session.setAttribute("firstName", userDetails.getFirstName());
            session.setAttribute("lastName", userDetails.getLastName());
            session.setAttribute("rankName", userDetails.getRankName());
            session.setAttribute("rankName", userDetails.getRankName());

            // 모델에 추가하여 뷰로 전달
            model.addAttribute("empNo", empNo);
            model.addAttribute("firstName", userDetails.getFirstName());
            model.addAttribute("lastName", userDetails.getLastName());
            model.addAttribute("rankName", userDetails.getRankName());

            // 프로젝트 데이터 가져오기 (사용자의 프로젝트)
            List<ProjectDTO> projects = projectService.getProjectsByUser(empNo); // 프로젝트 서비스에서 데이터 호출
            model.addAttribute("projects", projects);
        }
        return "index"; // index.html 템플릿으로 이동
    }



}
