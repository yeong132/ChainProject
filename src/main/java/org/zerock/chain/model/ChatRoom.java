package org.zerock.chain.model;

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
    private String senderEmpNo; // 발신자

    @Column(name = "recipient_emp_no")
    private String recipientEmpNo; // 수신자

    @Column(name = "unread_count", nullable = false)
    private int unreadCount = 0; // 읽지 않은 메시지 수
}
