package org.zerock.chain.dto;


import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long projectNo;
    private String projectName;


    // 날짜 형식 변환 어노테이션 사용

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectEndDate;

    private String dmpNo;
    private String participants;
    private String projectContent;
    private String projectFiles;

    // 기본 값 설정
    private boolean projectFavorite = false;
    private int projectProgress;
    private boolean isTemporary; // 임시 보관 여부

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
