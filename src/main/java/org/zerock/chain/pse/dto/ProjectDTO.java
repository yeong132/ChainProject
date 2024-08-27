package org.zerock.chain.pse.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private Long projectNo;
    private String projectName;

    private Integer empNo = 1; // 기본값 설정

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
