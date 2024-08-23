package org.zerock.chain.parkyeongmin.service;

import org.zerock.chain.parkyeongmin.dto.DocumentsDTO;

public interface ApprovalService {
    // 결재 요청을 처리하는 메서드
    void requestApproval(DocumentsDTO documentsDTO);

    // 결재를 승인하는 메서드
    void approveDocument(int docNo, Long empNo);

    // 결재를 반려하는 메서드
    void rejectDocument(int docNo, Long empNo, String rejectionReason);

    // 문서를 다음 결재자에게 할당하는 메서드
    void moveToNextApprover(int docNo, int nextOrder);

    // 문서를 최종 승인 상태로 변경하는 메서드
    void finalizeDocument(int docNo);
}
