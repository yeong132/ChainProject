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

    @GetMapping("/mypage")
    public String userMypage(Model model) {
        return "user/mypage";
    }

    @GetMapping("/setting")
    public String userSetting(Model model) {
        return "user/setting";
    }

    @GetMapping("/Q&A")
    public String getAllFAQs(Model model) {
        List<FavoriteQnaDTO> faqs = favoriteQnaService.getAllFAQs();
        model.addAttribute("faqs", faqs);
        return "user/Q&A";
    }

    @PostMapping("/add")
    public String registerFAQ(@RequestParam String faqName, @RequestParam String faqContent, Model model) {
        FavoriteQnaRequestDTO requestDTO = new FavoriteQnaRequestDTO();
        requestDTO.setFaqName(faqName);
        requestDTO.setFaqContent(faqContent);

        favoriteQnaService.createFAQ(requestDTO);
        return "redirect:/user/Q&A";
    }

    @PostMapping("/edit")
    public String editFaq(@RequestParam("faqNo") Long faqNo, @RequestParam("faqName") String faqName, @RequestParam("faqContent") String faqContent) {
        favoriteQnaService.updateFaq(faqNo, faqName, faqContent);
        return "redirect:/user/Q&A";
    }

    @PostMapping("/delete")
    public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
        favoriteQnaService.deleteFaq(faqNo);
        return "redirect:/user/Q&A";
    }

    @GetMapping("/qaRegister")
    public String userQARegister(Model model) {
        return "user/qaRegister";
    }

    @GetMapping("/qaDetail")
    public String userQADetail(Model model) {
        return "user/qaDetail";
    }

    @GetMapping("/logout")
    public String userLogout(Model model) {
        return "user/logout";
    }

    @GetMapping("/login")
    public String userLogin(Model model) {
        return "user/login";
    }

    @GetMapping("/alarm")
    public String userAlarm(Model model) {
        return "user/alarm";
    }
}
