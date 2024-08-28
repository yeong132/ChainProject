package org.zerock.chain.parkyeongmin.dto;

import java.time.LocalDateTime;

public class NotificationDTO {

    private long notificationNo;
    private Integer empNo;
    private String notificationType;
    private long referenceId;
    private String  notificationMessage;
    private LocalDateTime notificationDate;
    private Boolean isRead;
}
