package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.FavoriteQnaDTO;
import org.zerock.chain.dto.FavoriteQnaRequestDTO;
import org.zerock.chain.dto.QnaDTO;
import org.zerock.chain.dto.QnaRequestDTO;
import org.zerock.chain.service.FavoriteQnaService;
import org.zerock.chain.service.QnaService;

import java.util.List;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    private final FavoriteQnaService favoriteQnaService;
    private final QnaService qnaService;

    @Autowired
    public UserController(FavoriteQnaService favoriteQnaService, QnaService qnaService) {
        this.favoriteQnaService = favoriteQnaService;
        this.qnaService = qnaService;
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

    // Q&A 및 FAQ 목록을 가져와서 Q&A 페이지로 이동
    @GetMapping("/Q&A")
    public String getAllQnasAndFaqs(Model model) {
        List<QnaDTO> qnaList = qnaService.getAllQnas();
        List<FavoriteQnaDTO> faqList = favoriteQnaService.getAllFAQs();

        model.addAttribute("qnaList", qnaList);
        model.addAttribute("faqList", faqList);
        return "user/Q&A";
    }

    // 새로운 FAQ(자주 묻는 질문) 등록
    @PostMapping("/faq/add")
    public String registerFAQ(@RequestParam String faqName, @RequestParam String faqContent) {
        FavoriteQnaRequestDTO requestDTO = new FavoriteQnaRequestDTO();
        requestDTO.setFaqName(faqName);
        requestDTO.setFaqContent(faqContent);
        favoriteQnaService.createFAQ(requestDTO);
        return "redirect:/user/Q&A"; // 등록 후 Q&A 페이지로 리다이렉트
    }

    // 기존 FAQ(자주 묻는 질문) 수정
    @PostMapping("/faq/edit")
    public String editFaq(@RequestParam("faqNo") Long faqNo, @RequestParam("faqName") String faqName, @RequestParam("faqContent") String faqContent) {
        favoriteQnaService.updateFaq(faqNo, faqName, faqContent);
        return "redirect:/user/Q&A"; // 수정 후 Q&A 페이지로 리다이렉트
    }

    // FAQ(자주 묻는 질문) 삭제
    @PostMapping("/faq/delete")
    public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
        favoriteQnaService.deleteFaq(faqNo);
        return "redirect:/user/Q&A"; // 삭제 후 Q&A 페이지로 리다이렉트
    }

    /// Q&A 상세 페이지 조회
    @GetMapping("/qna/detail/{qnaNo}")
    public String detailQna(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        model.addAttribute("qna", qna);
        return "user/qaDetail"; // Q&A 상세 페이지로 이동
    }


    // Q&A 질문 등록 페이지로 이동
    @GetMapping("/qna/register")
    public String showQnaRegisterPage(Model model) {
        model.addAttribute("qnaRequestDTO", new QnaRequestDTO());
        return "user/qaRegister"; // Q&A 등록 페이지로 이동
    }

    // Q&A 질문 등록 처리
    @PostMapping("/qna/register")
    public String registerQna(@ModelAttribute QnaRequestDTO qnaRequestDTO) {
        QnaDTO createdQna = qnaService.createQna(qnaRequestDTO);
        return "redirect:/user/qna/detail/" + createdQna.getQnaNo(); // 등록 후 해당 Q&A 상세 페이지로 리다이렉트
    }


    // Q&A 수정 페이지로 이동 (수정 조회)
    @GetMapping("/qna/edit/{qnaNo}")
    public String editQnaPage(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        model.addAttribute("qna", qna);
        return "user/qaModify"; // 수정 페이지로 이동
    }

    // Q&A 수정 처리
    @PostMapping("/qna/edit/{qnaNo}")
    public String updateQna(@PathVariable Long qnaNo, @ModelAttribute QnaRequestDTO qnaRequestDTO) {
        qnaService.updateQna(qnaNo, qnaRequestDTO);
        return "redirect:/user/qna/detail/" + qnaNo; // 수정 후 해당 Q&A 상세 페이지로 리다이렉트
    }

    // Q&A 삭제 처리
    @DeleteMapping("/qna/delete/{qnaNo}")
    @ResponseBody
    public String deleteQna(@PathVariable Long qnaNo) {
        qnaService.deleteQna(qnaNo);
        return "redirect:/user/Q&A"; // 삭제 후 Q&A 페이지로 리다이렉트
    }

    // 로그아웃 페이지로 이동
    @GetMapping("/logout")
    public String userLogout(Model model) {
        return "user/logout";
    }

    // 로그인 페이지로 이동
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
