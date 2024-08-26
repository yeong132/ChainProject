package org.zerock.chain.pse.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.pse.dto.NoticeDTO;
import org.zerock.chain.pse.dto.NoticeRequestDTO;
import org.zerock.chain.pse.service.NoticeService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/notice")
@Log4j2
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // 공지사항 전체 목록 조회
    @GetMapping("/list")
    public String getAllNotices(Model model) {
        List<NoticeDTO> notices = noticeService.getAllNotices();

        // 고정값 순서와 최신순으로 정렬
        notices.sort(Comparator
                .comparing(NoticeDTO::getNoticePinned).reversed() // 고정된 항목이 먼저 오도록 정렬
                .thenComparing(Comparator.comparing(NoticeDTO::getNoticeCreatedDate).reversed())); // 그 다음 최신순으로 정렬

        model.addAttribute("notices", notices);
        return "notice/list";
    }


    // 개별 공지사항 상세 조회
    @GetMapping("/detail/{noticeNo}")
    public String getNoticeBynoticeNo(@PathVariable("noticeNo") Long noticeNo, Model model) {
        NoticeDTO notice = noticeService.getNoticeById(noticeNo);
        model.addAttribute("notice", notice);
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
    public String createNotice(@Valid @ModelAttribute NoticeRequestDTO noticeRequestDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "notice/register";  // 입력값에 에러가 있을 경우 등록 페이지로 돌아감
        }
        NoticeDTO createdNotice = noticeService.createNotice(noticeRequestDTO);
        return "redirect:/notice/detail/" + createdNotice.getNoticeNo();  // 성공적으로 생성된 경우 상세 페이지로 리디렉션
    }

    // 공지사항 수정 페이지 표시
    @GetMapping("/modify/{noticeNo}")
    public String showModifyPage(@PathVariable("noticeNo") Long noticeNo, Model model, RedirectAttributes redirectAttributes) {
        NoticeDTO notice = noticeService.getNoticeById(noticeNo);
        if (notice == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항을 찾을 수 없습니다.");
            return "redirect:/notice/list";
        }
        model.addAttribute("notice", notice);  // 여기서도 일관성 있게 사용
        return "notice/modify";
    }

    // 공지사항 수정
    @PostMapping("/modify/{noticeNo}")
    public String updateNotice(@PathVariable("noticeNo") Long noticeNo, @Valid @ModelAttribute NoticeRequestDTO noticeRequestDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "notice/modify";
        }
        noticeService.updateNotice(noticeNo, noticeRequestDTO);
        return "redirect:/notice/detail/" + noticeNo;
    }

    // 공지사항 삭제
    @PostMapping("/delete/{noticeNo}")
    public String deleteNotice(@PathVariable("noticeNo") Long noticeNo, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(noticeNo);
            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 삭제에 실패했습니다.");
            log.error("Error deleting notice", e);
        }
        return "redirect:/notice/list";
    }
}
