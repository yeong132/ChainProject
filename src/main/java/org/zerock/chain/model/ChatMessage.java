package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_no")
    private String chatNo; // 방

    @Column(name = "sender_emp_no")
    private String senderEmpNo; // 발신자

    @Column(name = "recipient_emp_no")
    private String recipientEmpNo; // 수신자

    @ManyToOne
    @JoinColumn(name = "chat_room_no", referencedColumnName = "id")
    private ChatRoom chatRoom; // 방번호

    @Column(name = "chat_content")
    private String chatContent; // 내용

    @Column(name = "chat_sent_time")
    private Date chatSentTime; // 보낸 시간

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;  // 메시지 확인 여부
}
