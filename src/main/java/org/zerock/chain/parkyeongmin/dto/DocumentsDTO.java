package org.zerock.chain.parkyeongmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsDTO {
    private Integer docNo;            // 문서 번호
    private Long loggedInEmpNo;       // 로그인한 사원 번호
    private String senderName;        // 결재문서 작성자
    private String senderDmpName;     // 작성자 부서
    private String senderRankName;    // 작성자 직급
    private String docTitle;          // 문서 제목
    private String docStatus;         // 문서 상태
    private String docBody;           // 문서 내용
    private String approvalLine;      // 결재선(결재자 이름) HTML 부분
    private String timeStampHtml;     // 타임스탬프 HTML 부분
    private String approverNoHtml;    // 결재자 순번 HTML 부분
    private MultipartFile file;       // 첨부 파일
    private String filePath;          // 파일 경로
    private LocalDate reqDate;        // 요청, 저장일
    private LocalDate reReqDate;      // 재요청일
    private String category;          // 양식 종류
    private String approversJson;     // 엔티티에 없는 부분 - 결재자 목록을 JSON 형식으로 저장하는 필드
    private String referencesJson;     // 엔티티에 없는 부분 - 참조자 번호를 JSON 형식으로 저장하는 필드
    private int approvalOrder;        // 다른 엔티티 부분 - 결재 순서
    private String approvalStatus;    // 다른 엔티티 부분 - 결재 상태
    private String rejectionReason;   // 다른 엔티티 부분 - 반려 사유
}