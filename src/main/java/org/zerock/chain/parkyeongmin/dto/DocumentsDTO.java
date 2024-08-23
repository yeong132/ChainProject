package org.zerock.chain.parkyeongmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsDTO {
    private Integer docNo;            // 문서 번호
    private Long loggedInEmpNo;       // 로그인한 사원 번호
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
    private String senderName;        // 엔티티에 없는 부분 sql로 작업할 것 - 걸재문서를 보낸 사람
    private String category;          // 양식 종류
}