package org.zerock.chain.pse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_no")
    private long notificationNo;

    @Column(name = "emp_no")
    private Integer empNo;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "reference_id")
    private long referenceId;

    @Column(name = "notification_message")
    private String  notificationMessage;

    @Column(name = "notification_date", insertable = false, updatable = false)
    private LocalDateTime notificationDate;

    @Column(name = "is_read")
    private Boolean isRead = false;

    public String getRedirectUrl() {
        switch (this.notificationType) {
            case "프로젝트":
                return "/project/detail/" + this.referenceId; // 프로젝트 관련 페이지로 이동
            case "공지사항":
                return "/notice/detail/" + this.referenceId; // 공지사항 관련 페이지로 이동
            case "업무보고서":
                return "/report/detail/" + this.referenceId; // 업무 보고서 관련 페이지로 이동
            default:
                return "/user/alarm"; // 기본적으로 알림 페이지로 이동
        }
    }
}
