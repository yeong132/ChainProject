package org.zerock.chain.junhyuck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zerock.chain.junhyuck.model.Signup;
import org.zerock.chain.junhyuck.repository.SignupRepository;
import org.zerock.chain.pse.model.CustomUserDetails;

@ControllerAdvice
public class GlobalModelAttributes {
// 각 페이지마다 Controller을 쓰지 않아도 모든 페이지에 이름과 사원번호를 불러오게 할 수 있는 Controller

    @Autowired
    private SignupRepository signupRepository;

    @ModelAttribute("fullName")
    public String getFullName(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long empNo = userDetails.getEmpNo();

            Signup user = signupRepository.findByEmpNo(empNo)
                    .orElse(null);

            if (user != null) {
                return user.getFullName();
            }
        }
        return ""; // 기본 값 또는 인증되지 않은 사용자의 경우
    }

    @ModelAttribute("empNo")
    public Long getEmpNo(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getEmpNo();
        }
        return null; // 인증되지 않은 사용자의 경우
    }
}