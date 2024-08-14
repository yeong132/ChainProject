package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomNo;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_type", nullable = false)
    private boolean roomType;  // 0: 일반 / 1: 즐겨찾기

    @Column(name = "recent_active_time")
    private LocalDateTime recentActiveTime;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages;

    public boolean getRoomType() {
        return roomType;
    }
}
