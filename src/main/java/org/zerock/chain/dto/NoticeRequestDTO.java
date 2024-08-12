package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeRequestDTO {

    private Long noticeNo;
    private String noticeName;
    private String noticeContent;
    private Boolean noticePinned;
    private String noticeFiles;
    private String noticeAuthor;
    private LocalDate noticePinnedDate;
}
