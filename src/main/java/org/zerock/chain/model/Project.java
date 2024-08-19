package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project")
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_no")
    private Long projectNo;

    @Column(length = 255, nullable = false, name = "project_name")
    private String projectName;

    @Column(name = "emp_no")
    private Integer empNo = 1; // Integer로 변경하고 기본값 설정

    @Column(name = "project_start_date")
    private LocalDate projectStartDate;

    @Column(name = "project_end_date")
    private LocalDate projectEndDate;

    @Column(length = 100, name = "dmp_no")
    private String dmpNo;

    @Column(columnDefinition = "TEXT")
    private String participants;

    @Column(name = "project_favorite", nullable = false)
    private boolean projectFavorite = false;

    @Column(name = "project_progress")
    private Integer projectProgress = 0;

    @Column(name = "project_content")
    private String projectContent;

    @Column(name = "project_files")
    private String projectFiles;

    @Column(name = "is_temporary")  // 임시 보관 여부
    private boolean isTemporary;

    public void setProjectFavorite(boolean projectFavorite) {
        this.projectFavorite = projectFavorite;
    }

    public void setProjectProgress(Integer projectProgress) {
        this.projectProgress = projectProgress;
    }

    public void setIsTemporary(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }
}
