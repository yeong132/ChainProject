package org.zerock.chain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProjectRequestDTO {
    private Long projectNo;
    private String projectName;
    private String projectStartDate;
    private String projectEndDate;
    private String dmpNo;
    private String participants;
    private String projectContent;
    private String projectFiles;
    private boolean projectFavorite;
    private int projectProgress;
    private boolean isTemporary; // 임시 보관

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
