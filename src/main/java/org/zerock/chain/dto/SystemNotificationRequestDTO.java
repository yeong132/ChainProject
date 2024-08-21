package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SystemNotificationRequestDTO {

    private Long systemNo;
    private String systemCategory;
    private String systemTitle;
    private String systemContent;
    private LocalDate systemUploadDate;

}
