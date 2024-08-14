package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartRequestDTO {

    private Long chartNo;          // 차트 번호
    private String chartName;      // 차트 이름
    private String chartContent; // 차트 내용
    private LocalDate chartStartDate; // 시작일
    private LocalDate chartEndDate;   // 종료일
    private String chartCategory;  // 카테고리
    private String chartProgress;  // 진행도
    private Boolean noticePinned;  // 완료 여부
    private String chartAuthor;    // 작성자
    private Long projectNo;        // 프로젝트 번호

    // 각 라벨을 받을 필드 추가
    private String progressLabel20 = "20";
    private String progressLabel40 = "40";
    private String progressLabel60 = "60";
    private String progressLabel80 = "80";
    private String progressLabel100 = "100";
}
