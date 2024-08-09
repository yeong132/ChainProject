package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.FavoriteQnaDTO;
import org.zerock.chain.dto.FavoriteQnaRequestDTO;
import org.zerock.chain.service.FavoriteQnaService;

import java.util.List;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    private final FavoriteQnaService favoriteQnaService;

    @Autowired
    public UserController(FavoriteQnaService favoriteQnaService) {
        this.favoriteQnaService = favoriteQnaService;
    }

    // 마이페이지로 이동
    @GetMapping("/mypage")
    public String userMypage(Model model) {
        return "user/mypage";
    }

    // 설정 페이지로 이동
    @GetMapping("/setting")
    public String userSetting(Model model) {
        return "user/setting";
    }

    // 모든 FAQ 목록을 가져와서 Q&A 페이지로 이동
    @GetMapping("/Q&A")
    public String getAllFAQs(Model model) {
        List<FavoriteQnaDTO> faqs = favoriteQnaService.getAllFAQs();
        model.addAttribute("faqs", faqs);
        return "user/Q&A";
    }

   // 새로운 FAQ 등록
    @PostMapping("/add")
    public String registerFAQ(@RequestParam String faqName, @RequestParam String faqContent, Model model) {
        FavoriteQnaRequestDTO requestDTO = new FavoriteQnaRequestDTO();
        requestDTO.setFaqName(faqName);
        requestDTO.setFaqContent(faqContent);

        favoriteQnaService.createFAQ(requestDTO);
        return "redirect:/user/Q&A";
    }

    // 기존 FAQ 수정
    @PostMapping("/faq/edit")
    public String editFaq(@RequestParam("faqNo") Long faqNo, @RequestParam("faqName") String faqName, @RequestParam("faqContent") String faqContent) {
        favoriteQnaService.updateFaq(faqNo, faqName, faqContent);
        return "redirect:/user/Q&A";
    }

    //FAQ 삭제
    @PostMapping("/faq/delete")
    public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
        favoriteQnaService.deleteFaq(faqNo);
        return "redirect:/user/Q&A";
    }

   // 질문 등록 페이지로 이동
    @GetMapping("/qaRegister")
    public String userQARegister(Model model) {
        return "user/qaRegister";
    }

    // 질문 상세 페이지로 이동
    @GetMapping("/qaDetail")
    public String userQADetail(Model model) {
        return "user/qaDetail";
    }

    // 로그아웃 페이지로 이동
    @GetMapping("/logout")
    public String userLogout(Model model) {
        return "user/logout";
    }

    //로그인 페이지로 이동
    @GetMapping("/login")
    public String userLogin(Model model) {
        return "user/login";
    }

    // 알림 페이지로 이동
    @GetMapping("/alarm")
    public String userAlarm(Model model) {
        return "user/alarm";
    }
}
