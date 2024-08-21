package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.CommentDTO;
import org.zerock.chain.dto.FavoriteQnaDTO;
import org.zerock.chain.dto.FavoriteQnaRequestDTO;
import org.zerock.chain.dto.QnaDTO;
import org.zerock.chain.dto.QnaRequestDTO;
import org.zerock.chain.model.Notification;
import org.zerock.chain.model.SystemNotification;
import org.zerock.chain.service.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    private final FavoriteQnaService favoriteQnaService;
    private final QnaService qnaService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final SystemNotificationService systemNotificationService;


    @Autowired
    public UserController(FavoriteQnaService favoriteQnaService, QnaService qnaService, CommentService commentService, NotificationService notificationService, SystemNotificationService systemNotificationService) {
        this.favoriteQnaService = favoriteQnaService;
        this.qnaService = qnaService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.systemNotificationService = systemNotificationService;
    }


    // Q&A 상세 페이지 조회
    @GetMapping("/qna/detail/{qnaNo}")
    public String detailQna(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        List<CommentDTO> comments = commentService.getCommentsByQnaNo(qnaNo); // 댓글 조회
        model.addAttribute("qna", qna);
        model.addAttribute("comments", comments); // 댓글 리스트 추가
        return "user/qaDetail"; // Q&A 상세 페이지로 이동
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




    // 시스템 알림 작성 처리
    @PostMapping("/systemNotification")
    public String createSystemNotification(
            @RequestParam("systemCategory") String systemCategory,
            @RequestParam("systemTitle") String systemTitle,
            @RequestParam("systemContent") String systemContent) {

        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setSystemCategory(systemCategory);
        systemNotification.setSystemTitle(systemTitle);
        systemNotification.setSystemContent(systemContent);
        systemNotification.setSystemUploadDate(LocalDate.now());

        systemNotificationService.saveSystemNotification(systemNotification);
        return "redirect:/user/alarm"; // 작성 후 알림 페이지로 리다이렉트
    }

    // 알림 페이지로 이동
    @GetMapping("/alarm")
    public String userAlarm(Model model) {
        int empNo = 1; // 예시 사원번호

        // 모든 알림과 프로젝트 알림을 각각 가져옵니다.
        List<Notification> allNotifications = notificationService.getAllNotifications(empNo);
        List<Notification> projectNotifications = notificationService.getNotificationsByType(empNo,"프로젝트");
        List<Notification> noticeNotifications = notificationService.getNotificationsByType(empNo,"공지사항");
        List<Notification> reportNotifications = notificationService.getNotificationsByType(empNo,"업무보고서");
        // 시스템 알림도 가져옵니다.
        List<SystemNotification> systemNotifications = systemNotificationService.getAllSystemNotifications();

        // 모든 알림에 시스템 알림을 추가합니다.
        model.addAttribute("allNotifications", allNotifications);
        model.addAttribute("projectNotifications", projectNotifications);
        model.addAttribute("noticeNotifications", noticeNotifications);
        model.addAttribute("reportNotifications", reportNotifications);
        model.addAttribute("systemNotifications", systemNotifications);

        return "user/alarm";
    }

    // 알림 전체 삭제
    @PostMapping("/alarm/deleteAll")
    public String deleteAllNotifications() {
        int empNo = 1; // 예시 사원번호

        // 일반 알림 삭제
        notificationService.deleteAllNotifications(empNo);
        // 시스템 알림 삭제
        systemNotificationService.deleteAllSystemNotifications();

        return "redirect:/user/alarm";
    }

    // 개별 알림 삭제
    @PostMapping("/alarm/delete/{notificationNo}")
    public String deleteNotification(@PathVariable Long notificationNo) {
        notificationService.deleteNotification(notificationNo);
        return "redirect:/user/alarm";
    }

    // 개별 시스템 알림 삭제
    @PostMapping("/systemNotification/delete/{systemNo}")
    public String deleteSystemNotification(@PathVariable Long systemNo) {
        systemNotificationService.deleteSystemNotification(systemNo);
        return "redirect:/user/alarm";
    }

}
