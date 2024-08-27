package org.zerock.chain.pse.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long projectNo;
    private String projectName;

    private Long empNo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectEndDate;

    private String dmpNo;
    private String participants;
    private String projectContent;
    private String projectFiles;
    private LocalDateTime uploadDate = LocalDateTime.now(); // 기본값 설정

    private boolean projectFavorite = false;
    private int projectProgress;
    private boolean isTemporary; // 임시 보관 여부
    private String progressLabels; // 진행도 라벨들을 쉼표로 구분된 문자열로 저장

    // 각 라벨을 받을 필드 추가
    private String progressLabel20 = "20";
    private String progressLabel40 = "40";
    private String progressLabel60 = "60";
    private String progressLabel80 = "80";
    private String progressLabel100 = "100";

    public void setIsTemporary(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }
    public void setProjectFavorite(boolean projectFavorite) {
        this.projectFavorite = projectFavorite;
    }
    public void setProjectProgress(Integer projectProgress) {
        this.projectProgress = projectProgress;
    }

}
