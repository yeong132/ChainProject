package org.zerock.chain.imjongha.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.chain.imjongha.model.Employee;
import org.zerock.chain.imjongha.repository.EmployeeRepository;
import org.zerock.chain.parkyeongmin.dto.DocumentsDTO;
import org.zerock.chain.parkyeongmin.model.Approval;
import org.zerock.chain.parkyeongmin.model.Documents;
import org.zerock.chain.parkyeongmin.repository.ApprovalRepository;
import org.zerock.chain.parkyeongmin.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/attendance")
@Log4j2
public class CompanyController {
//
//    // 근태 상세 페이지
//    @GetMapping("/employee")
//    public String atDepartment() {
//        return "admin/attendance/employee";
//    }
//
//    // 근태 전체 페이지
//    @GetMapping("/company")
//    public String atEmployee() {
//        return "/admin/attendance/company";
//    }
    // 영민이 추가
    private final UserService userService;
    private final ApprovalRepository approvalRepository;
    private final EmployeeRepository employeeRepository;

    public CompanyController(UserService userService, ApprovalRepository approvalRepository, EmployeeRepository employeeRepository) {
        this.userService = userService;
        this.approvalRepository = approvalRepository;
        this.employeeRepository = employeeRepository;
    }

    // 사원 휴가 페이지
    @GetMapping("/emp_leave")
    public String empLeave() {
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
}
