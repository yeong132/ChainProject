package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Getter
@Builder
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

    @Column(name = "project_start_date")
    private LocalDate  projectStartDate;

    @Column(name = "project_end_date")
    private LocalDate  projectEndDate;

    @Column(length = 100, name = "dmp_no")
    private String dmpNo;

    @Column(columnDefinition = "TEXT")
    private String participants;

    @Column(name = "project_favorite")
    private Boolean projectFavorite;

    @Column(name = "project_progress")
    private Integer projectProgress;

}
