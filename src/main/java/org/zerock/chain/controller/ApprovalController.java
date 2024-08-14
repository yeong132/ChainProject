package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.domain.DocumentsEntity;
import org.zerock.chain.dto.*;
import org.zerock.chain.service.DocumentsService;
import org.zerock.chain.service.FormDataService;
import org.zerock.chain.service.FormFieldsService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/approval")
@Log4j2
public class ApprovalController {

    private final DocumentsService documentsService;
    private final FormDataService formDataService;
    private final FormFieldsService formFieldsService;

    @Autowired
    public ApprovalController(DocumentsService documentsService, FormDataService formDataService, FormFieldsService formFieldsService) {
        this.documentsService = documentsService;
        this.formDataService = formDataService;
        this.formFieldsService = formFieldsService;
    }

    @GetMapping("/main")                                         // 보낸 문서함 페이지로 이동
    public String approvalMain(@RequestParam(value = "senderEmpNo", defaultValue = "1") Integer senderEmpNo, Model model) {
        // 보낸 문서함에 대한 문서 목록을 조회
        List<SentDocumentsDTO> sentDocuments = documentsService.getSentDocuments(senderEmpNo);
        model.addAttribute("sentDocuments", sentDocuments);
        return "approval/main";
    }

    @GetMapping("/receive")                                      // 받은 문서함 페이지로 이동
    public String approvalReceive(@RequestParam(value = "receiverEmpNo", defaultValue = "1") Integer receiverEmpNo, Model model) {
        // 받은 문서함에 대한 문서 목록을 조회
        List<ReceiveDocumentsDTO> receivedDocuments = documentsService.getReceivedDocuments(receiverEmpNo);
        log.info(receivedDocuments);
        model.addAttribute("receivedDocuments", receivedDocuments);
        return "approval/receive";
    }

    @GetMapping("/draft")                                        // 임시 문서함 페이지로 이동
    public String approvalDraft(Model model) {
        // 임시 문서함에 대한 문서 목록을 조회
        List<DraftDocumentsDTO> draftDocuments = documentsService.getDraftDocuments();
        log.info(draftDocuments);
        model.addAttribute("draftDocuments", draftDocuments);
        return "approval/draft";
    }

    @GetMapping("/adminRequest")
    public String approvalAdminRequest() { return "approval/adminRequest"; }

    @GetMapping("/completedRead")
    public String approvalCompletedRead() { return "approval/completedRead"; }

    @GetMapping("/draftRead")
    public String approvalDraftRead() { return "approval/draftRead"; }

    @GetMapping("/process")
    public String approvalProcess(@RequestParam("file") String file,
                                  @RequestParam("docNo") int docNo,
                                  @RequestParam("category") String category,
                                  Model model) {
        // 필요에 따라 docNo와 category를 모델에 추가
        model.addAttribute("docNo", docNo);
        model.addAttribute("category", category);

        // 추가적으로 필요하면 문서 정보를 조회하여 모델에 추가
        DocumentsDTO document = documentsService.getDocumentById(docNo);
        model.addAttribute("document", document);
        // 파일 파라미터를 모델에 추가
        model.addAttribute("file", file);
        return "approval/process";
    }

    @GetMapping("/read")
    public String approvalRead() { return "approval/read"; }

    @GetMapping("/rejectionRead")
    public String approvalRejectionRead() { return "approval/rejectionRead"; }

    @GetMapping("/generalApproval")
    public String approvalGeneralApproval() { return "approval/generalApproval"; }

    @GetMapping("/expense")
    public String approvalExpense() { return "approval/expense"; }

    @GetMapping("/overTime")
    public String approvalOverTime() { return "approval/overTime"; }

    // 여기서 부터 document 관련 메서드 입니다!!
    @GetMapping("/read/{docNo}")
    public String getDocumentDetail(@PathVariable("docNo") int docNo, Model model) {
        // 문서 번호를 모델에 추가
        model.addAttribute("docNo", docNo);

        // 문서와 관련된 카테고리 정보를 가져옴
        String category = documentsService.getCategoryByDocNo(docNo);
        model.addAttribute("category", category);

        // 양식 정보를 가져옴
        List<FormFieldsDTO> formFields = formFieldsService.getFormFieldsByCategory(category);
        model.addAttribute("formFields", formFields);

        // 카테고리에 따라 템플릿 이름을 결정
        String templateName = getTemplateNameByCategory(category);
        return templateName;  // 동적으로 결정된 템플릿 파일의 이름을 반환
    }

    @PostMapping("/submit")
    public String submitForm(@RequestBody SubmitRequest submitRequest) {

        DocumentsEntity documentsEntity = new DocumentsEntity();
        documentsEntity.setDocTitle(submitRequest.getDocTitle()); // 제목 설정
        documentsEntity.setReqDate(LocalDate.now()); // 현재 날짜로 설정
        documentsEntity.setFormNo(1); // 예시로 고정값 사용
        documentsEntity.setSenderEmpNo(1); // 예시로 보내는 사원 번호 설정
        documentsEntity.setReceiverEmpNo(2); // 예시로 받는 사원 번호 설정
        documentsEntity.setCategory("일반기안서"); // 예시로 고정값 사용

        int docNo = documentsService.saveDocument(documentsEntity, submitRequest.getFormFields(), submitRequest.getFormData());

        return "redirect:/approval/main"; // main.html로 리다이렉트
    }

    @PostMapping("/create-document")
    @ResponseBody // 이 메서드는 JSON 데이터를 반환하게 함
    public ResponseEntity<Map<String, Integer>> createDocument(@RequestBody DocumentsEntity documentsEntity) {
        // 문서 엔티티 생성 및 카테고리 설정
        documentsEntity.setReqDate(LocalDate.now()); // 현재 날짜를 저장

        // 문서 저장 후 문서 번호 반환
        int savedDocument = documentsService.saveDocument(documentsEntity, null, null); // 문서 저장

        // 저장된 문서의 번호 반환
        Map<String, Integer> response = new HashMap<>();
        response.put("docNo", savedDocument);
        return ResponseEntity.ok(response);
    }

    // category에 기반하여 템플릿 이름을 결정하는 로직
    private String getTemplateNameByCategory(String category) {
        switch (category) {
            case "일반기안서":
                return "generalApproval.html";   // 일반 기안
            case "지출결의서":
                return "expense.html";           // 지출결의서
            case "연장근무신청서":
                return "overTime.html";          // 연장근무 신청서
            default:
                return "default";  // 기본 템플릿
        }
    }
}
