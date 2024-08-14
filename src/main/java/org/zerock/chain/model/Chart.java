package org.zerock.chain.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chart")
@ToString
public class Chart {

    // 목표차트는 데이터 생성, 프로젝트 차트는 기존 데이터 가져오기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chart_no")
    private Long chartNo; // 차트 번호

    @Column(name = "chart_name")
    private String chartName; // 차트 이름

    @Column(name = "chart_content")
    private String chartContent; // 차트 내용

    @Column(name = "chart_start_date")
    private LocalDate chartStartDate;  // 시작일

    @Column(name = "chart_end_date")
    private LocalDate chartEndDate;// 종료일

    @Column(name = "chart_category")
    private String chartCategory;// 카테고리

    @Column(name = "chart_progress")
    private String chartProgress ;   // 진행도

    @Column(name = "notice_pinned")
    private Boolean noticePinned = false;   // 완료 여부

    @Column(name = "chart_upload_date")
    private LocalDate reportUploadDate= LocalDate.now();    // 생성일

    @Column(name = "chart_author")
    private String chartAuthor  ;  //  작성자

    @Column(name = "progress_labels")
    private String progressLabels; // 진행도 라벨들을 쉼표로 구분된 문자열로 저장

}
