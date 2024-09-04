package org.zerock.chain.imjongha.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.imjongha.model.Employee;
import org.zerock.chain.imjongha.model.EmployeeLeave;
import org.zerock.chain.imjongha.repository.EmployeeRepository;
import org.zerock.chain.imjongha.service.EmployeeLeaveService;
import org.zerock.chain.parkyeongmin.dto.DocumentsDTO;
import org.zerock.chain.parkyeongmin.model.Approval;
import org.zerock.chain.parkyeongmin.model.Documents;
import org.zerock.chain.parkyeongmin.repository.ApprovalRepository;
import org.zerock.chain.parkyeongmin.repository.EmployeesRepository;
import org.zerock.chain.parkyeongmin.service.DocumentsService;
import org.zerock.chain.parkyeongmin.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/attendance")
@Log4j2
public class CompanyController {

    // 영민이 추가
    private final UserService userService;
    private final EmployeeLeaveService employeeLeaveService;
    private final DocumentsService documentsService;
    private final ApprovalRepository approvalRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeesRepository employeesRepository;

    public CompanyController(UserService userService, EmployeeLeaveService employeeLeaveService, DocumentsService documentsService, ApprovalRepository approvalRepository, EmployeeRepository employeeRepository, EmployeesRepository employeesRepository) {
        this.userService = userService;
        this.employeeLeaveService = employeeLeaveService;
        this.documentsService = documentsService;
        this.approvalRepository = approvalRepository;
        this.employeeRepository = employeeRepository;
        this.employeesRepository = employeesRepository;
    }

    // 각 사용자의 연차 신청서 내역(영민 추가)
    @GetMapping("/emp_leave/{empNo}")
    public String empLeave(@PathVariable("empNo") Long empNo, Model model) {
        // URL에 있는 empNo로 해당 사용자의 보낸 문서 가져오기
        List<DocumentsDTO> sentDocuments = documentsService.getSentDocuments(empNo);

        // 보낸 문서 중 연차신청서만 필터링
        List<DocumentsDTO> leaveDocuments = sentDocuments.stream()
                .filter(document -> "연차신청서".equals(document.getCategory()))
                .collect(Collectors.toList());

        // 가상 번호 부여 (최신순)
        int virtualNo = leaveDocuments.size();
        for (DocumentsDTO document : leaveDocuments) {
            document.setVirtualNo(virtualNo--);  // 가상의 번호를 설정
            log.info("leaveDocument: {}, virtualNo: {}", document.getDocNo(), document.getVirtualNo());
        }

        // empNo를 사용하여 사원의 전체 이름을 가져옴
        String fullName = employeesRepository.findFullNameByEmpNo(empNo);

        // fullName도 뷰로 전달
        model.addAttribute("fullName", fullName);
        // 필터링된 문서 리스트를 모델에 추가하여 뷰로 전달
        model.addAttribute("leaveDocuments", leaveDocuments);
        return "/admin/attendance/emp_leave";
    }

    // 연차 신청서 페이지(영민 추가)
    @GetMapping("/leave")
    public String atLeave(Model model) {
        // 로그인한 사용자의 정보를 userService의 getLoggedInUserEmpNo 메서드로 가져오기
        Long loggedInEmpNo = userService.getLoggedInUserEmpNo();

        // 로그인한 사용자가 결재자 또는 참조자인 문서를 docNo 기준으로 최신순 조회
        List<Approval> approvals = approvalRepository.findByEmployeeOrRefEmployee(loggedInEmpNo);

        // Approval 엔티티에서 DocumentsDTO로 변환
        List<DocumentsDTO> receivedDocuments = approvals.stream()
                .map(approval -> convertToDocumentsDTO(approval.getDocuments()))  // Approval 엔티티에서 Documents 엔티티로 접근
                .collect(Collectors.toList());

        // 가상 번호 부여 (최신순 정렬에 따라 부여)
        int virtualNo = receivedDocuments.size();
        for (DocumentsDTO document : receivedDocuments) {
            document.setVirtualNo(virtualNo--);  // 가상의 번호를 설정
            log.info("leaveDocument: {}, leaveVirtualNo: {}", document.getDocNo(), document.getVirtualNo());
        }

        List<Employee> employeeLeaveList = employeeRepository.findAll();
        model.addAttribute("employeeLeaveList", employeeLeaveList);
        model.addAttribute("receivedDocuments", receivedDocuments);

        return "admin/attendance/leave";
    }

    // DocumentsDTO로 변환하는 메서드(영민 추가)
    private DocumentsDTO convertToDocumentsDTO(Documents document) {
        DocumentsDTO documentsDTO = new DocumentsDTO();

        // Documents 엔티티의 필드를 DocumentsDTO에 복사
        documentsDTO.setDocNo(document.getDocNo());
        documentsDTO.setDocTitle(document.getDocTitle());
        documentsDTO.setDocBody(document.getDocBody());
        documentsDTO.setSenderName(document.getSenderName()); // 예시로 sender의 이름을 가져옴
        documentsDTO.setReqDate(document.getReqDate());
        documentsDTO.setCategory(document.getCategory());
        documentsDTO.setDocStatus(document.getDocStatus());
        // 추가적인 필드가 있으면 여기에 추가

        return documentsDTO;
    }

    // 사용자 연차 정보 프론트로 주는 메서드(영민 추가)
    @GetMapping("/emp_leave/{empNo}/leaveInfo")
    @ResponseBody
    public Map<String, Object> getEmployeeLeaveInfo(@PathVariable("empNo") Long empNo) {
        EmployeeLeave employeeLeave = employeeLeaveService.getEmployeeLeaveByEmpNo(empNo);
        String fullName = employeesRepository.findFullNameByEmpNo(empNo);

        Map<String, Object> leaveInfo = new HashMap<>();
        leaveInfo.put("usedLeaveDays", employeeLeave.getUsedLeaveDays());  // 사용 연차
        leaveInfo.put("unusedLeaveDays", employeeLeave.getUnusedLeaveDays());  // 미사용 연차
        leaveInfo.put("fullName", fullName); // 사용자 이름 추가

        return leaveInfo;
    }

    // 모달창에서 업데이트 된 사용 연차 일수 변경에 따른 연차 정보 반환하는 메서드(영민 추가)
    @PostMapping("/emp_leave/{empNo}/update")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> updateEmployeeLeave(@PathVariable("empNo") Long empNo, @RequestBody Map<String, Integer> requestData) {
        int usedLeaveDays = requestData.get("usedLeaveDays");

        // 서비스 레이어에서 데이터 업데이트 처리 및 결과 반환
        Map<String, Integer> updatedLeaveData = employeeLeaveService.updateUsedLeaveDays(empNo, usedLeaveDays);

        if (updatedLeaveData != null) {
            return ResponseEntity.ok(updatedLeaveData);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
