package org.zerock.chain.service;

import org.zerock.chain.domain.DocumentsEntity;
import org.zerock.chain.dto.DocumentsDTO;
import org.zerock.chain.dto.FormDataDTO;
import org.zerock.chain.dto.FormFieldsDTO;

import java.util.List;
import java.util.Map;

public interface DocumentsService <T extends DocumentsDTO> {
    T getDocumentById(int docNo);
    List<T> getAllDocuments();
    int registerDocument(T documentsDTO);

    // 문서번호에 기반하여 카테고리를 가져오는 메소드 추가
    String getCategoryByDocNo(int docNo);
    // 사용자가 문서에 입력한 모든 정보를 반환하는 메소드 추가
    int saveDocument(DocumentsEntity documentsEntity, List<FormFieldsDTO> formFields, Map<Integer, String> formData);

    List<T> getSentDocuments(Integer senderEmpNo);  // 보낸 문서 목록 조회
    List<T> getReceivedDocuments(Integer receiverEmpNo);  // 받은 문서 목록 조회
    List<T> getDraftDocuments();  // 임시 문서 목록 조회
    // 필요한 메서드 정의 기능
}
