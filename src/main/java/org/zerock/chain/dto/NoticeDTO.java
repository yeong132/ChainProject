package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NoticeDTO {

    private Long noticeNo;
    private String noticeName;
    private String noticeContent;
    private Boolean noticePinned;
    private LocalDateTime noticeCreatedDate;
    private String noticeFiles;
    private String noticeAuthor;
    private LocalDate noticePinnedDate;
}
