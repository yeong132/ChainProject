package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequestDTO {

    private String noticeName;
    private String noticeContent;
    private String noticeFiles;
    private String noticeAuthor;
    private LocalDate noticePinnedDate;
}