package org.zerock.chain.dto;

import java.time.LocalDateTime;

public class NotificationRequestDTO {

    private long notificationNo;
    private Integer empNo;
    private String notificationType;
    private long referenceId;
    private String  notificationMessage;
    private LocalDateTime notificationDate;
    private Boolean isRead;
}
