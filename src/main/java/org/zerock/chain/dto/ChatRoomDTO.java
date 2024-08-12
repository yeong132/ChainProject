package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/* 채팅방 관리 */
// Stomp를 통해 topic/app 를 사용하면 구독자 관리가 알아서 됨
// 따라서 따로 세션 관리를 하는 코드를 작성할 필요가 없고, 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현 필요 없음
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long chatRoomNo; // 채팅방 번호
    private String roomName; // 채팅방 이름
    private boolean roomType;
    private LocalDateTime recentActiveTime; // 최근 활동 시간
    private Integer unreadCount;  // 읽지 않은 메시지 수
}
