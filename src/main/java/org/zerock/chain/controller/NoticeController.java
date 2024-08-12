package org.zerock.chain.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.NoticeRequestDTO;
import org.zerock.chain.service.NoticeService;

@Controller
@RequestMapping("/notice")
@Log4j2
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지사항 전체 목록 조회
    @GetMapping("/list")
    public String getAllNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "notice/list";
    }

    // 개별 공지사항 상세 조회
    @GetMapping("/detail/{noticeNo}")
    public String getNoticeBynoticeNo(@PathVariable("noticeNo") Long noticeNo, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(noticeNo));
        return "notice/detail";
    }

    // 공지사항 등록 페이지 표시
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("noticeRequestDTO", new NoticeRequestDTO());
        return "notice/register";
    }

    // 새 공지사항 생성
    @PostMapping("/register")
    public String createNotice(@Valid @ModelAttribute NoticeRequestDTO noticeRequestDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "notice/register";
        }
        noticeService.createNotice(noticeRequestDTO);
        return "redirect:/notice/list";
    }

    // 공지사항 수정 페이지 표시
    @GetMapping("/modify/{noticeNo}")
    public String showModifyPage(@PathVariable("noticeNo") Long noticeNo, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(noticeNo));
        return "notice/modify";
    }

    // 공지사항 수정
    @PostMapping("/modify/{noticeNo}")
    public String updateNotice(@PathVariable("noticeNo") Long noticeNo, @ModelAttribute NoticeRequestDTO noticeRequestDTO) {
        noticeService.updateNotice(noticeNo, noticeRequestDTO);
        return "redirect:/notice/detail/" + noticeNo;
    }

    // 공지사항 삭제
    @PostMapping("/delete/{noticeNo}")
    public String deleteNotice(@PathVariable("noticeNo") Long noticeNo) {
        noticeService.deleteNotice(noticeNo);
        return "redirect:/notice/list";
    }

}
