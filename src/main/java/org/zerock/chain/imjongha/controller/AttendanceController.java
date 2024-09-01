package org.zerock.chain.imjongha.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.imjongha.dto.AttendanceRecordDTO;
import org.zerock.chain.imjongha.dto.MonthlyAttendanceSummaryDTO;
import org.zerock.chain.imjongha.service.AttendanceRecordService;
import org.zerock.chain.imjongha.service.MonthlyAttendanceSummaryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/admin/attendance")
public class AttendanceController {

    private final MonthlyAttendanceSummaryService monthlyAttendanceSummaryService;
    private final AttendanceRecordService attendanceRecordService;

    public AttendanceController(MonthlyAttendanceSummaryService monthlyAttendanceSummaryService,
                                AttendanceRecordService attendanceRecordService) {
        this.monthlyAttendanceSummaryService = monthlyAttendanceSummaryService;
        this.attendanceRecordService = attendanceRecordService;
    }

    @GetMapping("/company")
    public String getCompanyAttendance(Model model) {
        log.info("getCompanyAttendance 호출됨");

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        List<MonthlyAttendanceSummaryDTO> summaries = monthlyAttendanceSummaryService.getSummariesByYearAndMonth(currentYear, currentMonth);
        Map<Integer, List<MonthlyAttendanceSummaryDTO>> summariesGroupedByMonth = summaries.stream()
                .collect(Collectors.groupingBy(MonthlyAttendanceSummaryDTO::getMonth));

        model.addAttribute("summariesGroupedByMonth", summariesGroupedByMonth);

        return "admin/attendance/company";
    }

    @GetMapping("/employee/{empNo}/monthly-summary")
    public String getEmployeeMonthlySummary(@PathVariable("empNo") Long empNo, Model model) {
        log.info("getEmployeeMonthlySummary 호출됨 - 사원 번호: {}", empNo);

        List<MonthlyAttendanceSummaryDTO> summaries = monthlyAttendanceSummaryService.getAllSummariesByEmployee(empNo);

        if (summaries == null || summaries.isEmpty()) {
            log.warn("summaries가 비어 있습니다. 사원 번호: {}", empNo);
        } else {
            log.info("summaries 데이터 크기: {}, 데이터: {}", summaries.size(), summaries);
        }

        model.addAttribute("summaries", summaries);

        return "admin/attendance/monthlySummary";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<AttendanceRecordDTO> getAttendanceRecordForEdit(@PathVariable("id") Long attendanceId) {
        log.info("getAttendanceRecordForEdit 호출됨 - 출근 기록 ID: {}", attendanceId);

        if (attendanceId == null || attendanceId <= 0) {
            log.warn("유효하지 않은 출근 기록 ID: {}", attendanceId);
            return ResponseEntity.badRequest().build();
        }

        AttendanceRecordDTO recordDTO = attendanceRecordService.getAttendanceById(attendanceId);
        if (recordDTO == null) {
            log.warn("출근 기록을 찾을 수 없습니다. ID: {}", attendanceId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(recordDTO);
    }
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> updateAttendanceRecord(@RequestParam("attendanceId") Long attendanceId,
                                                         @RequestParam("startTime") String startTime,
                                                         @RequestParam("endTime") String endTime,
                                                         @RequestParam("status") String status) {
        log.info("updateAttendanceRecord 호출됨 - 출근 기록 ID: {}, 시작 시간: {}, 종료 시간: {}, 상태: {}", attendanceId, startTime, endTime, status);

        attendanceRecordService.updateAttendanceRecord(attendanceId, startTime, endTime, status);

        return ResponseEntity.ok("출근 기록이 수정되었습니다.");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAttendanceRecord(@RequestParam("attendanceId") Long attendanceId) {
        log.info("deleteAttendanceRecord 호출됨 - 출근 기록 ID: {}", attendanceId);

        attendanceRecordService.deleteAttendanceRecord(attendanceId);

        return ResponseEntity.ok("출근 기록이 삭제되었습니다.");
    }


    @PostMapping("/check-in")
    public ResponseEntity<String> checkIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String empNo = authentication.getName();

        if (empNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사원번호를 찾을 수 없습니다.");
        }

        log.info("사원번호 {}로 출근 기록 생성 시도", empNo);

        try {
            attendanceRecordService.recordCheckIn(Long.parseLong(empNo));
            log.info("사원번호 {}로 출근 기록 생성 성공", empNo);
            return ResponseEntity.ok("출근 기록이 생성되었습니다.");
        } catch (Exception e) {
            log.error("사원번호 {}로 출근 기록 생성 중 오류 발생: {}", empNo, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("출근 기록 생성에 실패했습니다.");
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<String> checkOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String empNo = authentication.getName();

        if (empNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사원번호를 찾을 수 없습니다.");
        }

        log.info("사원번호 {}로 퇴근 기록 생성 시도", empNo);

        try {
            attendanceRecordService.recordCheckOut(Long.parseLong(empNo));
            log.info("사원번호 {}로 퇴근 기록 생성 성공", empNo);
            return ResponseEntity.ok("퇴근 기록이 생성되었습니다.");
        } catch (Exception e) {
            log.error("사원번호 {}로 퇴근 기록 생성 중 오류 발생: {}", empNo, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("퇴근 기록 생성에 실패했습니다.");
        }
    }
}
