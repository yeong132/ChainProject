package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatNo;

    @ManyToOne
    @JoinColumn(name = "chat_room_no")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee employee;

    @Column(name = "chat_content")
    private String chatContent;

    @Column(name = "chat_sent_time")
    private LocalDateTime chatSentTime;

    @Column(name = "chat_is_read", nullable = false)
    private boolean chatIsRead;
}
