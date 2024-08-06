package org.zerock.chain.dto;

import lombok.Data;

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

    // Getters and Setters
    public Long getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(Long projectNo) {
        this.projectNo = projectNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(String projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public String getProjectEndDate() {
        return projectEndDate;
    }

    public void setProjectEndDate(String projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public String getDmpNo() {
        return dmpNo;
    }

    public void setDmpNo(String dmpNo) {
        this.dmpNo = dmpNo;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getProjectContent() {
        return projectContent;
    }

    public void setProjectContent(String projectContent) {
        this.projectContent = projectContent;
    }

    public String getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(String projectFiles) {
        this.projectFiles = projectFiles;
    }

    public boolean isProjectFavorite() {
        return projectFavorite;
    }

    public void setProjectFavorite(boolean projectFavorite) {
        this.projectFavorite = projectFavorite;
    }

    public int getProjectProgress() {
        return projectProgress;
    }

    public void setProjectProgress(int projectProgress) {
        this.projectProgress = projectProgress;
    }
}
