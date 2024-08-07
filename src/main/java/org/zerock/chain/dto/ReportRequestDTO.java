package org.zerock.chain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Data
public class ReportRequestDTO {
    private String reportName;
    private String reportCategory;
    private String reportContent;
    private int reportAuthor;
    private Date reportUploadDate;
    private String reportFiles;
    private boolean isTemporary;
    private String reportParticipants;
    private String meetingTime;
    private String meetingRoom;
}
