package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequestDTO {

    private Long reportNo;

    private String reportName;

    private Integer empNo = 1; // Integer로 변경하고 기본값 설정

    private String reportCategory;
    private String reportContent;
    private String reportAuthor;
    private LocalDate reportUploadDate;
    private String reportFiles;
    private boolean isTemporary;
    private String reportParticipants;
    private String meetingTime;
    private String meetingRoom;

    public void setIsTemporary(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }
}
