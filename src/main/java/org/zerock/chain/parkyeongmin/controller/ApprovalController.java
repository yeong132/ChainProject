package org.zerock.chain.parkyeongmin.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.parkyeongmin.dto.*;
import org.zerock.chain.parkyeongmin.service.DocumentsService;
import org.zerock.chain.parkyeongmin.service.FormService;
import org.zerock.chain.parkyeongmin.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprovalController {

    private final DocumentsService documentsService;
    private final FormService formService;  // FormService 주입
    private final UserService userService;

    @Autowired
    public ApprovalController(DocumentsService documentsService,
                              FormService formService,
                              UserService userService) {
        this.documentsService = documentsService;
        this.formService = formService;
        this.userService = userService;
    }

    @GetMapping("/main")  // 보낸 문서함 페이지로 이동
    public String approvalMain(Model model) {
        Long loggedInEmpNo = 1L;
        List<SentDocumentsDTO> sentDocuments = documentsService.getSentDocuments(loggedInEmpNo);
        model.addAttribute("sentDocuments", sentDocuments);
        return "approval/main";
    }

    @GetMapping("/receive")  // 받은 문서함 페이지로 이동
    public String approvalReceive(Model model) {
        Long loggedInEmpNo = 1L;
        List<ReceiveDocumentsDTO> receivedDocuments = documentsService.getReceivedDocuments(loggedInEmpNo);
        log.info(receivedDocuments.toString());
        model.addAttribute("receivedDocuments", receivedDocuments);
        return "approval/receive";
    }

    @GetMapping("/draft")  // 임시 문서함 페이지로 이동
    public String approvalDraft(Model model) {
        Long loggedInEmpNo = 1L;
        List<DraftDocumentsDTO> draftDocuments = documentsService.getDraftDocuments(loggedInEmpNo);
        log.info(draftDocuments.toString());
        model.addAttribute("draftDocuments", draftDocuments);
        return "approval/draft";
    }

    @GetMapping("/adminRequest")
    public String approvalAdminRequest() {
        return "approval/adminRequest";
    }

    @GetMapping("/completedRead")
    public String approvalCompletedRead() {
        return "approval/completedRead";
    }

    @GetMapping("/draftRead")
    public String approvalDraftRead() {
        return "approval/draftRead";
    }

    @GetMapping("/process")
    public String approvalProcess(Model model) {
        return "approval/process";
    }

    @GetMapping("/read")
    public String approvalRead() {
        return "approval/read";
    }

    @GetMapping("/rejectionRead")
    public String approvalRejectionRead() {
        return "approval/rejectionRead";
    }

    @GetMapping("/generalApproval")
    public String approvalGeneralApproval() {
        return "approval/generalApproval";
    }

    @GetMapping("/expense")
    public String approvalExpense() {
        return "approval/expense";
    }

    @GetMapping("/overTime")
    public String approvalOverTime() {
        return "approval/overTime";
    }

    // 여기서 부터 document 관련 메서드 입니다!!
    @PostMapping("/create-document")
    public ResponseEntity<Map<String, Object>> createDocument(@ModelAttribute DocumentsDTO documentsDTO) {
        Map<String, Object> response = new HashMap<>();
        // 요청 데이터 출력
        log.info("DocumentsDTO: {}", documentsDTO);

        try {
            // 문서 저장 후 문서 번호 반환
            int docNo = documentsService.saveDocument(documentsDTO);

            response.put("docNo", docNo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while saving document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/draftRead/{docNo}")
    public String draftReadDocument(@PathVariable("docNo") int docNo,
                               Model model) {
        // 원래는 로그인한 사용자의 정보를 가져오는건데 임시로 emp_no가 1인 사용자 정보를 가져옴
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();
        // 문서 번호로 문서 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 사용자 정보를 추가
        model.addAttribute("loggedInUser", loggedInUser);
        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);

        // 'draftRead.html' 뷰를 반환
        return "/approval/draftRead";
    }

    @GetMapping("/read/{docNo}")
    public String readDocument(@PathVariable("docNo") int docNo,
                               Model model) {
        // 임시로 emp_no가 1인 사용자의 정보 가져오기
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();
        // 문서 번호로 문서 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 모델에 사용자 정보를 추가
        model.addAttribute("loggedInUser", loggedInUser);
        // 모델에 문서 데이터를 추가
        model.addAttribute("document", document);

        // 'read.html' 뷰를 반환
        return "/approval/read";
    }

    @GetMapping("/getForm/{category}")
    public ResponseEntity<FormDTO> getFormByCategory(@PathVariable String category) {
        log.info("Fetching form HTML for category: {}", category);

        FormDTO formDTO = formService.getFormByCategory(category);

        if (formDTO != null) {
            return ResponseEntity.ok(formDTO);
        } else {
            log.error("Form not found for category: {}", category);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getDocumentData/{docNo}")
    public ResponseEntity<Map<String, Object>> getDocumentData(@PathVariable("docNo") int docNo) {
        // 문서 정보 조회
        DocumentsDTO document = documentsService.getDocumentById(docNo);

        // 원래는 로그인한 사용자의 정보를 가져오는건데 임시로 emp_no가 1인 사용자 정보를 가져옴
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();

        // 반환할 데이터를 맵에 추가
        Map<String, Object> response = new HashMap<>();
        response.put("document", document);
        response.put("loggedInUser", loggedInUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        List<EmployeeDTO> employees = userService.getAllEmployees();
        // 원래는 로그인한 사용자의 정보를 가져오는건데 임시로 emp_no가 1인 사용자 정보를 가져옴
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();

        // 반환할 데이터를 맵에 추가
        Map<String, Object> response = new HashMap<>();
        response.put("employees", employees);
        response.put("loggedInUser", loggedInUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getEmployees")
    public ResponseEntity<EmployeeDTO> getLoggedInUserDetails() {
        // 원래는 로그인한 사용자의 정보를 가져오는건데 임시로 emp_no가 1인 사용자 정보를 가져옴
        EmployeeDTO loggedInUser = userService.getLoggedInUserDetails();

        return ResponseEntity.ok(loggedInUser);
    }

    @PostMapping("/update-document")
    public ResponseEntity<String> updateDocument(@ModelAttribute DocumentsDTO documentsDTO) {
        try {
            documentsService.updateDocument(documentsDTO);
            return ResponseEntity.ok("Document updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update document");
        }
    }

    @DeleteMapping("/delete-document/{docNo}")
    public ResponseEntity<String> deleteDocument(@PathVariable int docNo) {
        try {
            documentsService.deleteDocument(docNo);
            return ResponseEntity.ok("Document deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete document");
        }
    }
}