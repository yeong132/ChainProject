package org.zerock.chain.parkyeongmin.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentStatusCountDTO {
    private int requestsCount;    // 요청한 결재 문서 수
    private int inProgressCount;  // 진행 중인 결재 문서 수
    private int rejectedCount;    // 반려된 결재 문서 수
    private int completedCount;   // 완료된 결재 문서 수
}
