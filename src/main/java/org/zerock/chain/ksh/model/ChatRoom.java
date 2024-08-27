package org.zerock.chain.ksh.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_no")
    private String chatNo; // 방

    @Column(name = "sender_emp_no")
    private Long senderEmpNo; // 발신자의 emp_no

    @Column(name = "recipient_emp_no")
    private Long recipientEmpNo; // 수신자의 emp_no

    @Column(name = "unread_count", nullable = false)
    private int unreadCount = 0; // 읽지 않은 메시지 수
}
