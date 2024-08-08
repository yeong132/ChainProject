package org.zerock.chain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Data
public class ReportRequestDTO {
    private String reportName;
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
