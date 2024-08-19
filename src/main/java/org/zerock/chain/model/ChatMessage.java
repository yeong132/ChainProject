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
    private String chatNo;

    @Column(name = "sender_emp_no")
    private String senderEmpNo;

    @Column(name = "recipient_emp_no")
    private String recipientEmpNo;

    @ManyToOne
    @JoinColumn(name = "chat_room_no", referencedColumnName = "id")
    private ChatRoom chatRoom;

    @Column(name = "chat_content")
    private String chatContent;

    @Column(name = "chat_sent_time")
    private Date chatSentTime;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;  // 기본값은 false

//    @ManyToOne
//    @JoinColumn(name = "chat_room_no")
//    private ChatRoom chatRoom;

//    @ManyToOne
//    @JoinColumn(name = "emp_no")
//    private Employee employee;


}
