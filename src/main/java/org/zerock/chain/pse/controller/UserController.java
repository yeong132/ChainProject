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
        this.favoriteQnaService = favoriteQnaService;  // FavoriteQnaService 초기화
        this.qnaService = qnaService;  // QnaService 초기화
        this.commentService = commentService;  // CommentService 초기화
        this.notificationService = notificationService;  // NotificationService 초기화
        this.systemNotificationService = systemNotificationService;  // SystemNotificationService 초기화
        this.employeeService = employeeService;  // EmployeeService 초기화
        this.attendanceRecordService = attendanceRecordService;
        this.monthlyAttendanceSummaryService = monthlyAttendanceSummaryService;
    }

    // 환경설정에서 알람 온오프
    @PostMapping("/update-notification-setting")
    public ResponseEntity<?> updateNotificationSetting(@RequestBody Map<String, Object> payload) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        String notificationType = (String) payload.get("notificationType");
        Boolean enabled = (Boolean) payload.get("enabled");

        notificationService.updateNotificationSettingByType(empNo, notificationType, enabled);  // 알림 설정 업데이트

        return ResponseEntity.ok().build();  // 성공 응답 반환
    }

    // Q&A 상세 페이지 조회
    @GetMapping("/qna/detail/{qnaNo}")
    public String detailQna(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);  // 특정 QnA 번호로 QnA 조회
        List<CommentDTO> comments = commentService.getCommentsByQnaNo(qnaNo);  // QnA 번호로 댓글 조회
        model.addAttribute("qna", qna);  // 조회된 QnA를 모델에 추가
        model.addAttribute("comments", comments);  // 조회된 댓글을 모델에 추가
        return "user/qaDetail";  // QnA 상세 페이지로 이동
    }

    // 마이페이지 조회
    @GetMapping("/mypage")
    public String getMypage(Model model) {
        // SecurityContextHolder에서 현재 인증된 사용자의 이름(사원 번호)을 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String empNo = authentication.getName();

        if (empNo == null) {
            // 사원번호가 없으면 에러 처리
            model.addAttribute("employeeError", "로그인 정보가 없습니다.");
            return "error"; // 적절한 에러 페이지로 리다이렉트
        }

        try {
            // 사원 정보 조회
            EmployeeDTO employee = employeeService.getEmployeeById(Long.parseLong(empNo));
            model.addAttribute("employee", employee);

            // 현재 연도와 월에 해당하는 근태 요약 정보 가져오기
            LocalDate currentDate = LocalDate.now();
            List<MonthlyAttendanceSummaryDTO> monthlySummaries = monthlyAttendanceSummaryService.getSummariesByYearAndMonth(currentDate.getYear(), currentDate.getMonthValue());

            // 최근 월별 근태 요약 정보 가져오기
            MonthlyAttendanceSummaryDTO currentMonthSummary = monthlySummaries.stream().findFirst().orElse(null);
            model.addAttribute("monthlyAttendanceSummary", currentMonthSummary);

            // 오늘 날짜의 출퇴근 기록 가져오기
            AttendanceRecordDTO attendance = attendanceRecordService.getAttendanceRecordByDateAndEmpNo(currentDate, Long.parseLong(empNo));

            if (attendance == null) {
                // 출퇴근 기록이 없을 경우, 기본 메시지 또는 초기 상태 설정
                model.addAttribute("dailyAttendanceMessage", "오늘의 출퇴근 기록이 없습니다.");
            } else {
                // 출퇴근 기록이 있는 경우, 해당 기록을 모델에 추가
                model.addAttribute("dailyAttendance", attendance);
            }
        } catch (Exception e) {
            model.addAttribute("employeeError", "사원 정보를 찾을 수 없습니다.");
            log.error("사원 정보 조회 실패: {}", e.getMessage());
            return "error"; // 에러 페이지로 리다이렉트
        }

        return "user/mypage";
    }

    // 설정 페이지로 이동
    @GetMapping("/setting")
    public String userSetting(HttpSession session, Model model) {
        Long empNo = (Long) session.getAttribute("empNo");
        if (empNo == null) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }

        EmployeeDTO employee = employeeService.getEmployeeById(empNo);  // 사원 정보 조회
        model.addAttribute("employee", employee);  // 조회된 사원 정보를 모델에 추가

        return "user/setting";  // 설정 페이지로 이동
    }

    // 계정 정보 수정
    @PostMapping("/update")
    public String updateEmployee(
            @RequestParam("lastName") String lastName, @RequestParam("firstName") String firstName, @RequestParam("address") String address, @RequestParam("phone") String phone, @RequestParam("email") String email,
            HttpSession session,
            Model model) {

        Long empNo = (Long) session.getAttribute("empNo");

        EmployeeDTO existingEmployee = employeeService.getEmployeeById(empNo);  // 기존 사원 정보 조회

        existingEmployee.setLastName(lastName);  // 성 업데이트
        existingEmployee.setFirstName(firstName);  // 이름 업데이트
        existingEmployee.setAddr(address);  // 주소 업데이트
        existingEmployee.setPhoneNum(phone);  // 전화번호 업데이트
        existingEmployee.setEmail(email);  // 이메일 업데이트

        employeeService.updateEmployee(empNo, existingEmployee);  // 사원 정보 업데이트

        model.addAttribute("employee", existingEmployee);  // 업데이트된 사원 정보를 모델에 추가

        return "redirect:/user/setting";  // 설정 페이지로 리다이렉트
    }

    // Q&A 및 FAQ 목록을 가져와서 Q&A 페이지로 이동
    @GetMapping("/Q&A")
    public String getAllQnasAndFaqs(Model model) {
        List<QnaDTO> qnaList = qnaService.getAllQnas();  // 모든 QnA 조회
        List<FavoriteQnaDTO> faqList = favoriteQnaService.getAllFAQs();  // 모든 FAQ 조회

        qnaList.sort(Comparator.comparing(QnaDTO::getQnaUploadDate).reversed());  // 최신순으로 정렬

        model.addAttribute("qnaList", qnaList);  // 정렬된 QnA를 모델에 추가
        model.addAttribute("faqList", faqList);  // 정렬된 FAQ를 모델에 추가
        return "user/Q&A";  // Q&A 페이지로 이동
    }

    // 새로운 FAQ 등록
    @PostMapping("/faq/add")
    public String registerFAQ(@RequestParam String faqName, @RequestParam String faqContent) {
        FavoriteQnaRequestDTO requestDTO = new FavoriteQnaRequestDTO();
        requestDTO.setFaqName(faqName);  // FAQ 이름 설정
        requestDTO.setFaqContent(faqContent);  // FAQ 내용 설정
        favoriteQnaService.createFAQ(requestDTO);  // 새로운 FAQ 생성
        return "redirect:/user/Q&A";  // FAQ 등록 후 Q&A 페이지로 리다이렉트
    }

    // 기존 FAQ 수정
    @PostMapping("/faq/edit")
    public String editFaq(@RequestParam("faqNo") Long faqNo, @RequestParam("faqName") String faqName, @RequestParam("faqContent") String faqContent) {
        favoriteQnaService.updateFaq(faqNo, faqName, faqContent);  // FAQ 수정 처리
        return "redirect:/user/Q&A";  // 수정 후 Q&A 페이지로 리다이렉트
    }

    // FAQ 삭제
    @PostMapping("/faq/delete")
    public String deleteFaq(@RequestParam("faqNo") Long faqNo) {
        favoriteQnaService.deleteFaq(faqNo);  // FAQ 삭제 처리
        return "redirect:/user/Q&A";  // 삭제 후 Q&A 페이지로 리다이렉트
    }

    // Q&A 질문 등록 페이지로 이동
    @GetMapping("/qna/register")
    public String showQnaRegisterPage(Model model) {
        model.addAttribute("qnaRequestDTO", new QnaRequestDTO());  // 빈 QnA 요청 DTO를 모델에 추가
        return "user/qaRegister";  // QnA 등록 페이지로 이동
    }

    // Q&A 질문 등록 처리
    @PostMapping("/qna/register")
    public String registerQna(@ModelAttribute QnaRequestDTO qnaRequestDTO) {
        QnaDTO createdQna = qnaService.createQna(qnaRequestDTO);  // 새로운 QnA 생성
        return "redirect:/user/qna/detail/" + createdQna.getQnaNo();  // 생성된 QnA의 상세 페이지로 리다이렉트
    }

    // Q&A 수정 페이지로 이동
    @GetMapping("/qna/edit/{qnaNo}")
    public String editQnaPage(@PathVariable Long qnaNo, Model model) {
        QnaDTO qna = qnaService.getQnaById(qnaNo);  // 수정할 QnA 조회
        model.addAttribute("qna", qna);  // 조회된 QnA를 모델에 추가
        return "user/qaModify";  // QnA 수정 페이지로 이동
    }

    // Q&A 수정 처리
    @PostMapping("/qna/edit/{qnaNo}")
    public String updateQna(@PathVariable Long qnaNo, @ModelAttribute QnaRequestDTO qnaRequestDTO) {
        qnaService.updateQna(qnaNo, qnaRequestDTO);  // QnA 수정 처리
        return "redirect:/user/qna/detail/" + qnaNo;  // 수정 후 QnA 상세 페이지로 리다이렉트
    }

    // Q&A 삭제 처리
    @DeleteMapping("/qna/delete/{qnaNo}")
    @ResponseBody
    public String deleteQna(@PathVariable Long qnaNo) {
        qnaService.deleteQna(qnaNo);  // QnA 삭제 처리
        return "redirect:/user/Q&A";  // 삭제 후 Q&A 페이지로 리다이렉트
    }

    // 시스템 알림 작성 처리
    @PostMapping("/systemNotification")
    public String createSystemNotification(
            @RequestParam("systemCategory") String systemCategory,
            @RequestParam("systemTitle") String systemTitle,
            @RequestParam("systemContent") String systemContent) {

        // 시스템 알림 기본 정보 설정
        SystemNotification systemNotification = new SystemNotification();
        systemNotification.setSystemCategory(systemCategory);  // 시스템 알림 카테고리 설정
        systemNotification.setSystemTitle(systemTitle);  // 시스템 알림 제목 설정
        systemNotification.setSystemContent(systemContent);  // 시스템 알림 내용 설정
        systemNotification.setSystemUploadDate(LocalDateTime.now());  // 시스템 알림 업로드 날짜 설정
        systemNotification.setRead(false);  // 읽음 상태 false로 설정

        // 모든 사원의 empNo를 가져와서 개별 시스템 알림 생성
        List<EmployeeDTO> allEmployees = employeeService.getAllEmployees();
        for (EmployeeDTO employee : allEmployees) {
            // 사원별 시스템 알림 객체 생성
            SystemNotification individualNotification = new SystemNotification();
            individualNotification.setSystemCategory(systemCategory);  // 사원별 카테고리 설정
            individualNotification.setSystemTitle(systemTitle);  // 사원별 제목 설정
            individualNotification.setSystemContent(systemContent);  // 사원별 내용 설정
            individualNotification.setSystemUploadDate(LocalDateTime.now());  // 사원별 업로드 날짜 설정
            individualNotification.setRead(false);  // 사원별 읽음 상태 false로 설정
            individualNotification.setEmpNo(employee.getEmpNo());  // 개별 사원의 empNo 설정

            // 시스템 알림 저장
            systemNotificationService.saveSystemNotification(individualNotification);  // 시스템 알림 저장 처리
        }

        return "redirect:/user/alarm";  // 작성 후 알림 페이지로 리다이렉트
    }

    // 알림 페이지로 이동
    @GetMapping("/alarm")
    public String userAlarm(Model model) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        List<Notification> allNotifications = notificationService.getAllNotifications(empNo);  // 모든 알림 조회
        List<Notification> projectNotifications = notificationService.getNotificationsByType(empNo, "프로젝트");  // 프로젝트 알림 조회
        List<Notification> noticeNotifications = notificationService.getNotificationsByType(empNo, "공지사항");  // 공지사항 알림 조회
        List<Notification> reportNotifications = notificationService.getNotificationsByType(empNo, "업무보고서");  // 업무보고서 알림 조회
        List<Notification> chartNotifications = notificationService.getNotificationsByType(empNo, "차트");  // 차트 알림 조회
        List<Notification> accountNotifications = notificationService.getNotificationsByType(empNo, "계정"); // 계정 변경 알림 추가
        List<SystemNotification> systemNotifications = systemNotificationService.getAllSystemNotifications(empNo);  // 시스템 알림 조회

        allNotifications.sort(Comparator.comparing(Notification::getNotificationDate).reversed());  // 최신순으로 정렬
        systemNotifications.sort(Comparator.comparing(SystemNotification::getSystemUploadDate).reversed());  // 최신순으로 정렬

        model.addAttribute("allNotifications", allNotifications != null ? allNotifications : List.of());  // 모든 알림을 모델에 추가
        model.addAttribute("projectNotifications", projectNotifications != null ? projectNotifications : List.of());  // 프로젝트 알림을 모델에 추가
        model.addAttribute("noticeNotifications", noticeNotifications != null ? noticeNotifications : List.of());  // 공지사항 알림을 모델에 추가
        model.addAttribute("reportNotifications", reportNotifications != null ? reportNotifications : List.of());  // 업무보고서 알림을 모델에 추가
        model.addAttribute("chartNotifications", chartNotifications != null ? chartNotifications : List.of());  // 차트 알림을 모델에 추가
        model.addAttribute("accountNotifications", accountNotifications != null ? accountNotifications : List.of()); // 모델에 추가
        model.addAttribute("systemNotifications", systemNotifications != null ? systemNotifications : List.of());  // 시스템 알림을 모델에 추가

        return "user/alarm";  // 알림 페이지로 이동
    }


    // 알림 클릭 시 읽음 상태로 변경하고, 해당 페이지로 리다이렉트
    @GetMapping("/alarm/read/notification/{notificationNo}")
    public String readNotificationAndRedirect(@PathVariable("notificationNo") Long notificationNo) {
        // 메소드 구현
        Notification notification = notificationService.getNotificationById(notificationNo);// 특정 알림 번호로 알림 조회
        notificationService.markAsRead(notificationNo);// 알림 읽음 상태로 업데이트
        String redirectUrl = notification.getRedirectUrl();// 리다이렉트 URL 가져오기
        return "redirect:" + redirectUrl;// 해당 URL로 리다이렉트
    }


    // 시스템 알림 클릭 시 읽음 상태로 변경
    @GetMapping("/alarm/read/system/{systemNo}")
    public String readSystemAndRedirect(@PathVariable Long systemNo) {
        SystemNotification systemNotification = systemNotificationService.getSystemNotificationById(systemNo);  // 특정 시스템 알림 번호로 알림 조회
        systemNotificationService.markAsReadSystem(systemNo);  // 시스템 알림 읽음 상태로 업데이트

        return "redirect:/user/alarm";  // 알림 페이지로 리다이렉트
    }

    // 알림 전체 삭제
    @PostMapping("/alarm/deleteAll")
    public String deleteAllNotifications() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        notificationService.deleteAllNotifications(empNo);  // 모든 알림 삭제 처리
        systemNotificationService.deleteAllSystemNotifications();  // 모든 시스템 알림 삭제 처리

        return "redirect:/user/alarm";  // 삭제 후 알림 페이지로 리다이렉트
    }

    // 개별 알림 삭제
    @PostMapping("/alarm/delete/{notificationNo}")
    public String deleteNotification(@PathVariable Long notificationNo) {
        notificationService.deleteNotification(notificationNo);  // 특정 알림 삭제 처리
        return "redirect:/user/alarm";  // 삭제 후 알림 페이지로 리다이렉트
    }

    // 읽은 알림만 삭제
    @PostMapping("/alarm/deleteRead")
    public String deleteReadNotifications() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        Long empNo = (Long) session.getAttribute("empNo");

        notificationService.deleteReadNotifications(empNo);  // 읽은 알림 삭제 처리
        systemNotificationService.deleteReadSystemNotification(empNo);  // 읽은 시스템 알림 삭제 처리

        return "redirect:/user/alarm";  // 삭제 후 알림 페이지로 리다이렉트
    }

    // 개별 시스템 알림 삭제
    @PostMapping("/systemNotification/delete/{systemNo}")
    public String deleteSystemNotification(@PathVariable Long systemNo) {
        systemNotificationService.deleteSystemNotification(systemNo);  // 특정 시스템 알림 삭제 처리
        return "redirect:/user/alarm";  // 삭제 후 알림 페이지로 리다이렉트
    }
}
