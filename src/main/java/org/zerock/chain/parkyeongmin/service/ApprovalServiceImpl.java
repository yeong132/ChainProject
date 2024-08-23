package org.zerock.chain.parkyeongmin.service;

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

@Service
@Log4j2
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final DocumentsRepository documentsRepository;
    private final EmployeesRepository employeesRepository;

    @Override
    public void requestApproval(DocumentsDTO documentsDTO) {
        List<ApprovalDTO> approvers = parseApprovers(documentsDTO.getApproversJson());

        if (approvers.isEmpty()) {
            throw new RuntimeException("Approval line is empty. Please set up the approval line.");
        }

        // 결재선의 결재자들을 저장 (DocStatus는 변경하지 않음)
        for (ApprovalDTO approver : approvers) {
            // 문서와 사원 객체를 조회하여 설정
            Documents document = documentsRepository.findById(documentsDTO.getDocNo())
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            Employee employee = employeesRepository.findById(approver.getEmpNo())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Approval 객체 생성 및 설정
            Approval approval = new Approval();
            approval.setDocuments(document);  // 문서 객체 설정
            approval.setEmployee(employee);  // 사원 객체 설정
            approval.setApprovalOrder(approver.getApprovalOrder());
            approval.setApprovalStatus("대기");  // 초기 상태는 대기
            approvalRepository.save(approval);
        }

        // 첫 번째 결재자에게 문서 할당
        ApprovalDTO firstApprover = approvers.get(0);
        assignDocumentToApprover(documentsDTO.getDocNo(), firstApprover.getEmpNo());
    }

    @Override
    public void approveDocument(int docNo, Long empNo) {
        Approval approval = approvalRepository.findByDocNoAndEmpNo(docNo, empNo);

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
        Approval approval = approvalRepository.findByDocNoAndEmpNo(docNo, empNo);

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
        Approval nextApproval = approvalRepository.findByDocNoAndApprovalOrder(docNo, nextOrder);

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

    // 문서를 첫번째 결재자에게 할당하는 메서드
    private void assignDocumentToApprover(int docNo, Long empNo) {
        Documents document = documentsRepository.findById(docNo)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setReceiverEmpNo(empNo);  // 결재자(수신자)로 설정

        documentsRepository.save(document);

        System.out.println("Document No: " + docNo + " has been assigned to approver with empNo: " + empNo);
    }

    // 결재선을 파싱하여 결재자 목록을 반환하는 메서드
    private List<ApprovalDTO> parseApprovers(String approversJson) {
        // JSON을 파싱하여 결재자 목록을 반환하는 로직을 구현해야 합니다.
        // 예를 들어, Jackson 또는 Gson을 사용하여 JSON을 파싱할 수 있습니다.
        // 이 예제에서는 간단히 설명만 하며, 실제 구현은 생략합니다.
        return List.of(); // 실제로는 파싱된 ApprovalDTO 객체의 리스트를 반환해야 합니다.
    }

    // 반려 처리 로직 (임의로 추가)
    private void handleRejection(int docNo) {
        // 문서 반려 시 처리할 로직을 구현합니다.
        // 예를 들어, 작성자에게 알림을 보내거나, 문서 상태를 변경하는 등의 작업을 할 수 있습니다.
        System.out.println("Document No: " + docNo + " has been rejected.");
    }
}
