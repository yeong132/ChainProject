package org.zerock.chain.pse.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zerock.chain.imjongha.dto.EmployeeDTO;
import org.zerock.chain.imjongha.service.EmployeeService;
import org.zerock.chain.pse.dto.*;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.model.SystemNotification;
import org.zerock.chain.pse.service.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    private final FavoriteQnaService favoriteQnaService;
    private final QnaService qnaService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final SystemNotificationService systemNotificationService;
    private final EmployeeService employeeService;

    @Autowired
    public UserController(FavoriteQnaService favoriteQnaService, QnaService qnaService, CommentService commentService, NotificationService notificationService, SystemNotificationService systemNotificationService, EmployeeService employeeService) {
        this.favoriteQnaService = favoriteQnaService;
        this.qnaService = qnaService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.systemNotificationService = systemNotificationService;
        this.employeeService = employeeService;
    }

    // 환경설정에서 알람 온오프
    @PostMapping("/update-notification-setting")
    public ResponseEntity<?> updateNotificationSetting(@RequestBody Map<String, Object> payload) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        String notificationType = (String) payload.get("notificationType");
        Boolean enabled = (Boolean) payload.get("enabled");

        notificationService.updateNotificationSettingByType(empNo, notificationType, enabled);

        return ResponseEntity.ok().build();
    }

    // Q&A 상세 페이지 조회
    @GetMapping("/qna/detail/{qnaNo}")
    public String detailQna(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        List<CommentDTO> comments = commentService.getCommentsByQnaNo(qnaNo);
        model.addAttribute("qna", qna);
        model.addAttribute("comments", comments);
        return "user/qaDetail";
    }

    // 마이페이지로 이동
    @GetMapping("/mypage")
    public String userMypage(Model model) {
        return "user/mypage";
    }

    // 설정 페이지로 이동
    @GetMapping("/setting")
    public String userSetting(HttpSession session, Model model) {
        Long empNo = (Long) session.getAttribute("empNo");
        if (empNo == null) {
            return "redirect:/login";
        }

        EmployeeDTO employee = employeeService.getEmployeeById(empNo);
        model.addAttribute("employee", employee);

        return "user/setting";
    }

    // 계정 정보 수정
    @PostMapping("/update")
    public String updateEmployee(
            @RequestParam("lastName") String lastName, @RequestParam("firstName") String firstName, @RequestParam("address") String address, @RequestParam("phone") String phone, @RequestParam("email") String email,
            HttpSession session,
            Model model) {

        Long empNo = (Long) session.getAttribute("empNo");

        EmployeeDTO existingEmployee = employeeService.getEmployeeById(empNo);

        existingEmployee.setLastName(lastName);
        existingEmployee.setFirstName(firstName);
        existingEmployee.setAddr(address);
        existingEmployee.setPhoneNum(phone);
        existingEmployee.setEmail(email);

        employeeService.updateEmployee(empNo, existingEmployee);

        model.addAttribute("employee", existingEmployee);

        return "redirect:/user/setting";
    }

    // Q&A 및 FAQ 목록을 가져와서 Q&A 페이지로 이동
    @GetMapping("/Q&A")
    public String getAllQnasAndFaqs(Model model) {
        List<QnaDTO> qnaList = qnaService.getAllQnas();
        List<FavoriteQnaDTO> faqList = favoriteQnaService.getAllFAQs();

        qnaList.sort(Comparator.comparing(QnaDTO::getQnaUploadDate).reversed());

        model.addAttribute("qnaList", qnaList);
        model.addAttribute("faqList", faqList);
        return "user/Q&A";
    }

    // 새로운 FAQ 등록
    @PostMapping("/faq/add")
    public String registerFAQ(@RequestParam String faqName, @RequestParam String faqContent) {
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

    // FAQ 삭제
    @PostMapping("/faq/delete")
    public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
        favoriteQnaService.deleteFaq(faqNo);
        return "redirect:/user/Q&A";
    }

    // Q&A 질문 등록 페이지로 이동
    @GetMapping("/qna/register")
    public String showQnaRegisterPage(Model model) {
        model.addAttribute("qnaRequestDTO", new QnaRequestDTO());
        return "user/qaRegister";
    }

    // Q&A 질문 등록 처리
    @PostMapping("/qna/register")
    public String registerQna(@ModelAttribute QnaRequestDTO qnaRequestDTO) {
        QnaDTO createdQna = qnaService.createQna(qnaRequestDTO);
        return "redirect:/user/qna/detail/" + createdQna.getQnaNo();
    }

    // Q&A 수정 페이지로 이동
    @GetMapping("/qna/edit/{qnaNo}")
    public String editQnaPage(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        model.addAttribute("qna", qna);
        return "user/qaModify";
    }

    // Q&A 수정 처리
    @PostMapping("/qna/edit/{qnaNo}")
    public String updateQna(@PathVariable Long qnaNo, @ModelAttribute QnaRequestDTO qnaRequestDTO) {
        qnaService.updateQna(qnaNo, qnaRequestDTO);
        return "redirect:/user/qna/detail/" + qnaNo;
    }

    // Q&A 삭제 처리
    @DeleteMapping("/qna/delete/{qnaNo}")
    @ResponseBody
    public String deleteQna(@PathVariable Long qnaNo) {
        qnaService.deleteQna(qnaNo);
        return "redirect:/user/Q&A";
    }

    // 시스템 알림 작성 처리
    @PostMapping("/systemNotification")
    public String createSystemNotification(
            @RequestParam("systemCategory") String systemCategory,
            @RequestParam("systemTitle") String systemTitle,
            @RequestParam("systemContent") String systemContent) {

        // 시스템 알림 기본 정보 설정
        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setSystemCategory(systemCategory);
        systemNotification.setSystemTitle(systemTitle);
        systemNotification.setSystemContent(systemContent);
        systemNotification.setSystemUploadDate(LocalDateTime.now());
        systemNotification.setRead(false);

        // 모든 사원의 empNo를 가져와서 개별 시스템 알림 생성
        List<EmployeeDTO> allEmployees = employeeService.getAllEmployees();
        for (EmployeeDTO employee : allEmployees) {
            // 사원별 시스템 알림 객체 생성
            SystemNotification individualNotification = new SystemNotification();
            individualNotification.setSystemCategory(systemCategory);
            individualNotification.setSystemTitle(systemTitle);
            individualNotification.setSystemContent(systemContent);
            individualNotification.setSystemUploadDate(LocalDateTime.now());
            individualNotification.setRead(false);
            individualNotification.setEmpNo(employee.getEmpNo()); // 개별 사원의 empNo 설정

            // 시스템 알림 저장
            systemNotificationService.saveSystemNotification(individualNotification);
        }

        return "redirect:/user/alarm"; // 작성 후 알림 페이지로 리다이렉트
    }

    // 알림 페이지로 이동
    @GetMapping("/alarm")
    public String userAlarm(Model model) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        List<Notification> allNotifications = notificationService.getAllNotifications(empNo);
        List<Notification> projectNotifications = notificationService.getNotificationsByType(empNo, "프로젝트");
        List<Notification> noticeNotifications = notificationService.getNotificationsByType(empNo, "공지사항");
        List<Notification> reportNotifications = notificationService.getNotificationsByType(empNo, "업무보고서");
        List<Notification> chartNotifications = notificationService.getNotificationsByType(empNo, "차트");
        List<SystemNotification> systemNotifications = systemNotificationService.getAllSystemNotifications(empNo);

        allNotifications.sort(Comparator.comparing(Notification::getNotificationDate).reversed());
        systemNotifications.sort(Comparator.comparing(SystemNotification::getSystemUploadDate).reversed());

        model.addAttribute("allNotifications", allNotifications != null ? allNotifications : List.of());
        model.addAttribute("projectNotifications", projectNotifications != null ? projectNotifications : List.of());
        model.addAttribute("noticeNotifications", noticeNotifications != null ? noticeNotifications : List.of());
        model.addAttribute("reportNotifications", reportNotifications != null ? reportNotifications : List.of());
        model.addAttribute("chartNotifications", chartNotifications != null ? chartNotifications : List.of());
        model.addAttribute("systemNotifications", systemNotifications != null ? systemNotifications : List.of());

        return "user/alarm";
    }

    // 알림 클릭 시 읽음 상태로 변경하고, 해당 페이지로 리다이렉트
    @GetMapping("/alarm/read/notification/{notificationNo}")
    public String readNotificationAndRedirect(@PathVariable Long notificationNo) {
        Notification notification = notificationService.getNotificationById(notificationNo);
        notificationService.markAsRead(notificationNo);

        String redirectUrl = notification.getRedirectUrl();
        return "redirect:" + redirectUrl;
    }

    // 시스템 알림 클릭 시 읽음 상태로 변경
    @GetMapping("/alarm/read/system/{systemNo}")
    public String readSystemAndRedirect(@PathVariable Long systemNo) {
        SystemNotification systemNotification = systemNotificationService.getSystemNotificationById(systemNo);
        systemNotificationService.markAsReadSystem(systemNo);

        return "redirect:/user/alarm";
    }

    // 알림 전체 삭제
    @PostMapping("/alarm/deleteAll")
    public String deleteAllNotifications() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        notificationService.deleteAllNotifications(empNo);
        systemNotificationService.deleteAllSystemNotifications();

        return "redirect:/user/alarm";
    }

    // 개별 알림 삭제
    @PostMapping("/alarm/delete/{notificationNo}")
    public String deleteNotification(@PathVariable Long notificationNo) {
        notificationService.deleteNotification(notificationNo);
        return "redirect:/user/alarm";
    }

    // 읽은 알림만 삭제
    @PostMapping("/alarm/deleteRead")
    public String deleteReadNotifications() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        notificationService.deleteReadNotifications(empNo);
        systemNotificationService.deleteReadSystemNotification(empNo);

        return "redirect:/user/alarm";
    }

    // 개별 시스템 알림 삭제
    @PostMapping("/systemNotification/delete/{systemNo}")
    public String deleteSystemNotification(@PathVariable Long systemNo) {
        systemNotificationService.deleteSystemNotification(systemNo);
        return "redirect:/user/alarm";
    }
}
