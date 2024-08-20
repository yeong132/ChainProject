package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NoticeDTO {

    private Long noticeNo;

    private Integer empNo = 1; // Integer로 변경하고 기본값 설정

    private String noticeName;
    private String noticeContent;
    private Boolean noticePinned;
    private LocalDate noticeCreatedDate;
    private String noticeFiles;
    private String noticeAuthor;
    private LocalDate noticePinnedDate;
}
