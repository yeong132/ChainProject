package org.zerock.chain.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long projectNo;
    private String projectName;

    // 날짜 형식 변환 어노테이션 사용하기
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectEndDate;

    private String dmpNo;
    private String participants;

    // 기본 값 설정
    private boolean projectFavorite = false;
    private Integer projectProgress = 0;
}
