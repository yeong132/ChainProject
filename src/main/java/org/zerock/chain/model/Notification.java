package org.zerock.chain.model;

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

    @Column(name = "notification_date")
    private LocalDateTime notificationDate;

    @Column(name = "is_read")
    private Boolean isRead;
}
