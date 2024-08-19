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
    private String chatNo;

    @Column(name = "sender_emp_no")
    private String senderEmpNo;

    @Column(name = "recipient_emp_no")
    private String recipientEmpNo;

    @Column(name = "unread_count", nullable = false)
    private int unreadCount = 0; // 기본값 0

    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long chatRoomNo;
//    @Column(name = "room_name")
//    private String roomName;
//
//    @Column(name = "room_type", nullable = false)
//    private boolean roomType;  // 0: 일반 / 1: 즐겨찾기
//
//    @Column(name = "recent_active_time")
//    private LocalDateTime recentActiveTime;
//
//    @OneToMany(mappedBy = "chatRoom")
//    private List<ChatMessage> messages;
//
//    public boolean getRoomType() {
//        return roomType;
//    }
}
