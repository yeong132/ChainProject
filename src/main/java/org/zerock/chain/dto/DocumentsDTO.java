package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsDTO {
    private Integer docNo;            // 문서 번호
    private Integer formNo;           // 양식 번호
    private Integer senderEmpNo;      // 보낸 사원 번호
    private Integer receiverEmpNo;    // 받은 사원 번호
    private String docTitle;      // 문서 제목
    private String docStatus;     // 문서 상태
    private LocalDate reqDate;    // 요청일
    private LocalDate reReqDate;  // 재요청일
    private LocalDate draftDate;  // 임시저장일
    private String senderName;    // 엔티티에 없는 부분 sql로 작업할 것 - 걸재문서를 보낸 사람
    private String receiverName;  // 엔티티에 없는 부분 sql로 작업할 것 - 결재문서를 받은 사람
    private String category;      // 양식 종류

    /*private List<FormFieldsDTO> formFields; // 입력란 정보 리스트
    private Map<Integer, String> formData;  // 필드 번호와 값의 맵*/
}