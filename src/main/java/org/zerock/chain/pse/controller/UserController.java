package org.zerock.chain.pse.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zerock.chain.imjongha.dto.AttendanceRecordDTO;
import org.zerock.chain.imjongha.dto.EmployeeDTO;
import org.zerock.chain.imjongha.dto.MonthlyAttendanceSummaryDTO;
import org.zerock.chain.imjongha.service.AttendanceRecordService;
import org.zerock.chain.imjongha.service.EmployeeService;
import org.zerock.chain.imjongha.service.MonthlyAttendanceSummaryService;
import org.zerock.chain.pse.dto.*;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.model.SystemNotification;
import org.zerock.chain.pse.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    private final FavoriteQnaService favoriteQnaService;  // 즐겨찾기 QnA 서비스 의존성 주입
    private final QnaService qnaService;  // QnA 서비스 의존성 주입
    private final CommentService commentService;  // 댓글 서비스 의존성 주입
    private final NotificationService notificationService;  // 알림 서비스 의존성 주입
    private final SystemNotificationService systemNotificationService;  // 시스템 알림 서비스 의존성 주입
    private final EmployeeService employeeService;  // 사원 서비스 의존성 주입
    private final AttendanceRecordService attendanceRecordService;
    private final MonthlyAttendanceSummaryService monthlyAttendanceSummaryService;

    @Autowired
    public UserController(FavoriteQnaService favoriteQnaService, QnaService qnaService, CommentService commentService, NotificationService notificationService, SystemNotificationService systemNotificationService, EmployeeService employeeService, AttendanceRecordService attendanceRecordService, MonthlyAttendanceSummaryService monthlyAttendanceSummaryService) {
        this.favoriteQnaService = favoriteQnaService;
        this.qnaService = qnaService;
        this.commentService = commentService;
        this.notificationService = notificationService;
        this.systemNotificationService = systemNotificationService;
        this.employeeService = employeeService;
        this.attendanceRecordService = attendanceRecordService;
        this.monthlyAttendanceSummaryService = monthlyAttendanceSummaryService;
    }

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

    @GetMapping("/qna/detail/{qnaNo}")
    public String detailQna(@PathVariable("qnaNo") Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        List<CommentDTO> comments = commentService.getCommentsByQnaNo(qnaNo);
        model.addAttribute("qna", qna);
        model.addAttribute("comments", comments);
        return "user/qaDetail";
    }

    @GetMapping("/mypage")
    public String getMypage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String empNo = authentication.getName();

        if (empNo == null) {
            model.addAttribute("employeeError", "로그인 정보가 없습니다.");
            return "error";
        }

        try {
            EmployeeDTO employee = employeeService.getEmployeeById(Long.parseLong(empNo));
            model.addAttribute("employee", employee);

            LocalDate currentDate = LocalDate.now();
            List<MonthlyAttendanceSummaryDTO> monthlySummaries = monthlyAttendanceSummaryService.getSummariesByYearAndMonth(currentDate.getYear(), currentDate.getMonthValue());
            MonthlyAttendanceSummaryDTO currentMonthSummary = monthlySummaries.stream().findFirst().orElse(null);
            model.addAttribute("monthlyAttendanceSummary", currentMonthSummary);

            AttendanceRecordDTO attendance = attendanceRecordService.getAttendanceRecordByDateAndEmpNo(currentDate, Long.parseLong(empNo));

            if (attendance == null) {
                model.addAttribute("dailyAttendanceMessage", "오늘의 출퇴근 기록이 없습니다.");
            } else {
                model.addAttribute("dailyAttendance", attendance);
            }
        } catch (Exception e) {
            model.addAttribute("employeeError", "사원 정보를 찾을 수 없습니다.");
            log.error("사원 정보 조회 실패: {}", e.getMessage());
            return "error";
        }

        return "user/mypage";
    }

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

    @PostMapping("/update")
    public String updateEmployee(@RequestParam("lastName") String lastName,
                                 @RequestParam("firstName") String firstName,
                                 @RequestParam("address") String address,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("email") String email,
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

    @GetMapping("/Q&A")
    public String getAllQnasAndFaqs(Model model) {
        List<QnaDTO> qnaList = qnaService.getAllQnas();
        List<FavoriteQnaDTO> faqList = favoriteQnaService.getAllFAQs();

        qnaList.sort(Comparator.comparing(QnaDTO::getQnaUploadDate).reversed());
        model.addAttribute("qnaList", qnaList);
        model.addAttribute("faqList", faqList);
        return "user/Q&A";
    }

    @PostMapping("/faq/add")
    public String registerFAQ(@RequestParam String faqName, @RequestParam String faqContent) {
        FavoriteQnaRequestDTO requestDTO = new FavoriteQnaRequestDTO();
        requestDTO.setFaqName(faqName);
        requestDTO.setFaqContent(faqContent);
        favoriteQnaService.createFAQ(requestDTO);
        return "redirect:/user/Q&A";
    }

    @PostMapping("/faq/edit")
    public String editFaq(@RequestParam("faqNo") Long faqNo,
                          @RequestParam("faqName") String faqName,
                          @RequestParam("faqContent") String faqContent) {
        favoriteQnaService.updateFaq(faqNo, faqName, faqContent);
        return "redirect:/user/Q&A";
    }

    @PostMapping("/faq/delete")
    public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
        favoriteQnaService.deleteFaq(faqNo);
        return "redirect:/user/Q&A";
    }

    @GetMapping("/qna/register")
    public String showQnaRegisterPage(Model model) {
        model.addAttribute("qnaRequestDTO", new QnaRequestDTO());
        return "user/qaRegister";
    }

    @PostMapping("/qna/register")
    public String registerQna(@ModelAttribute QnaRequestDTO qnaRequestDTO) {
        QnaDTO createdQna = qnaService.createQna(qnaRequestDTO);
        return "redirect:/user/qna/detail/" + createdQna.getQnaNo();
    }

    @GetMapping("/qna/edit/{qnaNo}")
    public String editQnaPage(@PathVariable("qnaNo") Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);
        model.addAttribute("qna", qna);
        return "user/qaModify";
    }

    @PostMapping("/qna/edit/{qnaNo}")
    public String updateQna(@PathVariable("qnaNo") Long qnaNo, @ModelAttribute QnaRequestDTO qnaRequestDTO) {
        qnaService.updateQna(qnaNo, qnaRequestDTO);
        return "redirect:/user/qna/detail/" + qnaNo;
    }

    @DeleteMapping("/qna/delete/{qnaNo}")
    @ResponseBody
    public String deleteQna(@PathVariable("qnaNo") Long qnaNo) {
        qnaService.deleteQna(qnaNo);
        return "redirect:/user/Q&A";
    }

    @PostMapping("/systemNotification")
    public String createSystemNotification(@RequestParam("systemCategory") String systemCategory,
                                           @RequestParam("systemTitle") String systemTitle,
                                           @RequestParam("systemContent") String systemContent) {

        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setSystemCategory(systemCategory);
        systemNotification.setSystemTitle(systemTitle);
        systemNotification.setSystemContent(systemContent);
        systemNotification.setSystemUploadDate(LocalDateTime.now());
        systemNotification.setRead(false);

        List<EmployeeDTO> allEmployees = employeeService.getAllEmployees();
        for (EmployeeDTO employee : allEmployees) {
            SystemNotification individualNotification = new SystemNotification();
            individualNotification.setSystemCategory(systemCategory);
            individualNotification.setSystemTitle(systemTitle);
            individualNotification.setSystemContent(systemContent);
            individualNotification.setSystemUploadDate(LocalDateTime.now());
            individualNotification.setRead(false);
            individualNotification.setEmpNo(employee.getEmpNo());

            systemNotificationService.saveSystemNotification(individualNotification);
        }

        return "redirect:/user/alarm";
    }

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
        List<Notification> accountNotifications = notificationService.getNotificationsByType(empNo, "계정");
        List<SystemNotification> systemNotifications = systemNotificationService.getAllSystemNotifications(empNo);

        allNotifications.sort(Comparator.comparing(Notification::getNotificationDate).reversed());
        systemNotifications.sort(Comparator.comparing(SystemNotification::getSystemUploadDate).reversed());

        model.addAttribute("allNotifications", allNotifications != null ? allNotifications : List.of());
        model.addAttribute("projectNotifications", projectNotifications != null ? projectNotifications : List.of());
        model.addAttribute("noticeNotifications", noticeNotifications != null ? noticeNotifications : List.of());
        model.addAttribute("reportNotifications", reportNotifications != null ? reportNotifications : List.of());
        model.addAttribute("chartNotifications", chartNotifications != null ? chartNotifications : List.of());
        model.addAttribute("accountNotifications", accountNotifications != null ? accountNotifications : List.of());
        model.addAttribute("systemNotifications", systemNotifications != null ? systemNotifications : List.of());

        return "user/alarm";
    }

    @GetMapping("/alarm/read/notification/{notificationNo}")
    public String readNotificationAndRedirect(@PathVariable("notificationNo") Long notificationNo) {
        Notification notification = notificationService.getNotificationById(notificationNo);
        notificationService.markAsRead(notificationNo);
        String redirectUrl = notification.getRedirectUrl();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/alarm/read/system/{systemNo}")
    public String readSystemAndRedirect(@PathVariable("systemNo") Long systemNo) {
        SystemNotification systemNotification = systemNotificationService.getSystemNotificationById(systemNo);
        systemNotificationService.markAsReadSystem(systemNo);
        return "redirect:/user/alarm";
    }

    @PostMapping("/alarm/deleteAll")
    public String deleteAllNotifications() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        notificationService.deleteAllNotifications(empNo);
        systemNotificationService.deleteAllSystemNotifications();

        return "redirect:/user/alarm";
    }

    @PostMapping("/alarm/delete/{notificationNo}")
    public String deleteNotification(@PathVariable("notificationNo") Long notificationNo) {
        notificationService.deleteNotification(notificationNo);
        return "redirect:/user/alarm";
    }

    @PostMapping("/alarm/deleteRead")
    public String deleteReadNotifications() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        notificationService.deleteReadNotifications(empNo);
        systemNotificationService.deleteReadSystemNotification(empNo);

        return "redirect:/user/alarm";
    }

    @PostMapping("/systemNotification/delete/{systemNo}")
    public String deleteSystemNotification(@PathVariable("systemNo") Long systemNo) {
        systemNotificationService.deleteSystemNotification(systemNo);
        return "redirect:/user/alarm";
    }
}
