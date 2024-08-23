package org.zerock.chain.parkyeongmin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.chain.parkyeongmin.dto.ApprovalDTO;
import org.zerock.chain.parkyeongmin.dto.DocumentsDTO;
import org.zerock.chain.parkyeongmin.model.Approval;
import org.zerock.chain.parkyeongmin.model.Documents;
import org.zerock.chain.parkyeongmin.model.Employee;
import org.zerock.chain.parkyeongmin.repository.ApprovalRepository;
import org.zerock.chain.parkyeongmin.repository.DocumentsRepository;
import org.zerock.chain.parkyeongmin.repository.EmployeesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final DocumentsRepository documentsRepository;
    private final EmployeesRepository employeesRepository;

    // approvals테이블에 사용자가 정한 정보가 저장되는 메서드
    @Override
    public void requestApproval(DocumentsDTO documentsDTO) {
        // approverJson을 파싱하여 List<Map<String, Object>>로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> approvers;

        try {
            approvers = objectMapper.readValue(documentsDTO.getApproversJson(), new TypeReference<>() {});
            log.info("Parsed approvers: {}", approvers);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing approverJson", e);
        }

        if (approvers.isEmpty()) {
            log.warn("Approval line is empty. Throwing exception.");
            throw new RuntimeException("Approval line is empty. Please set up the approval line.");
        }

        // 결재선의 결재자들을 저장 (DocStatus는 변경하지 않음)
        for (Map<String, Object> approver : approvers) {
            // 문서와 사원 객체를 조회하여 설정
            Documents document = documentsRepository.findById(documentsDTO.getDocNo())
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            log.info("Found document: {}", document);

            // 문자열로 들어온 값을 Long과 Integer로 변환
            Long empNo = Long.valueOf(approver.get("empNo").toString());
            Integer approvalOrder = Integer.valueOf(approver.get("approvalOrder").toString());

            log.info("Processing approval for empNo: {}, approvalOrder: {}", empNo, approvalOrder);

            Employee employee = employeesRepository.findById(empNo)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            log.info("Found employee: {}", employee);

            // Approval 객체 생성 및 설정
            Approval approval = new Approval();
            approval.setDocuments(document);    // 문서 객체 설정
            approval.setEmployee(employee);     // 사원 객체 설정
            approval.setApprovalOrder(approvalOrder);
            approval.setApprovalStatus("대기");  // 초기 상태는 대기
            approvalRepository.save(approval);
            log.info("Saved approval: {}", approval);
        }

        // 첫 번째 결재자에게 문서 할당
        Map<String, Object> firstApprover = approvers.get(0);
        // String 타입을 Long으로 바꾸기 위한 코드
        String empNoStr = (String) firstApprover.get("empNo");
        Long firstEmpNo = Long.parseLong(empNoStr);
        assignDocumentToApprover(documentsDTO.getDocNo(), firstEmpNo);
        log.info("Assigned document {} to first approver with empNo: {}", documentsDTO.getDocNo(), firstEmpNo);
    }

    @Override
    public void approveDocument(int docNo, Long empNo) {
        Approval approval = approvalRepository.findByDocumentsDocNoAndEmployeeEmpNo(docNo, empNo);

        if (approval != null) {
            approval.setApprovalDate(LocalDateTime.now());
            approval.setApprovalStatus("승인");  // 승인 상태로 변경
            approvalRepository.save(approval);

            // 다음 결재자로 이동
            moveToNextApprover(docNo, approval.getApprovalOrder() + 1);
        }
    }

    @Override
    public void rejectDocument(int docNo, Long empNo, String rejectionReason) {
        Approval approval = approvalRepository.findByDocumentsDocNoAndEmployeeEmpNo(docNo, empNo);

        if (approval != null) {
            approval.setApprovalDate(LocalDateTime.now());
            approval.setApprovalStatus("반려");  // 반려 상태로 변경
            approval.setRejectionReason(rejectionReason);
            approvalRepository.save(approval);

            // 반려 처리 (작성자에게 알림 등)
            handleRejection(docNo);
        }
    }

    @Override
    public void moveToNextApprover(int docNo, int nextOrder) {
        Approval nextApproval = approvalRepository.findByDocumentsDocNoAndApprovalOrder(docNo, nextOrder);

        if (nextApproval != null) {
            // 다음 결재자가 있으면, 문서를 다음 결재자에게 할당하고, DocStatus를 "진행중"으로 변경
            assignDocumentToApprover(nextApproval.getDocuments().getDocNo(), nextApproval.getEmployee().getEmpNo());

            Documents document = documentsRepository.findById(docNo)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            document.setDocStatus("진행중");
            documentsRepository.save(document);
        } else {
            // 다음 결재자가 없으면, 문서를 최종 완료 상태로 변경
            finalizeDocument(docNo);
        }
    }

    @Override
    public void finalizeDocument(int docNo) {
        Documents document = documentsRepository.findById(docNo)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setDocStatus("결재 완료");
        documentsRepository.save(document);

        System.out.println("Document No: " + docNo + " has been finalized.");
    }

    // 문서를 첫 번째 결재자에게 할당하는 메서드 << 사실 필요없는데 위에 메서드에 들어가서 빨간줄 뜨니까 주말에 수정해보자
    private void assignDocumentToApprover(int docNo, Long empNo) {
        // 문서를 조회
        Documents document = documentsRepository.findById(docNo)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // 첫 번째 결재자를 조회 (approvalOrder가 1인 결재자)
        Approval firstApproval = approvalRepository.findByDocumentsDocNoAndApprovalOrder(docNo, 1);

        if (firstApproval == null) {
            throw new RuntimeException("Approval not found for the given document and approver.");
        }

        // Approval 객체에 필요한 로직 추가 (예: 문서 할당 상태 변경 등)
        // ...

        // 문서를 첫 번째 결재자에게 할당했다고 로그 출력
        System.out.println("Document No: " + docNo + " has been assigned to the first approver with empNo: " + empNo);
    }

    // 반려 처리 로직 (임의로 추가)
    private void handleRejection(int docNo) {
        // 문서 반려 시 처리할 로직을 구현합니다.
        // 예를 들어, 작성자에게 알림을 보내거나, 문서 상태를 변경하는 등의 작업을 할 수 있습니다.
        System.out.println("Document No: " + docNo + " has been rejected.");
    }
}
