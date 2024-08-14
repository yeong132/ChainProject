package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/* 채팅 메시지 관리 - 메시지 송수신을 위한 클래스 */
// 메시지 내용에 관한 데이터를 전달하기 위한 객체
// 작성 시간도 함께 전송
// 클라이언트에서는 JSON 형태로 메시지와 시간을 보내면 이를 다른 사용자에게 보여주는 형식으로 구현하기 위해 gson 사용
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long chatNo; // 메시지 번호
    private Long chatRoomNo; // 채팅방 번호
    private Long empNo; // 채팅을 보낸 사원 번호
    private String chatContent; // 메시지 내용
    private LocalDateTime chatSentTime; // 채팅 보낸 시간
    private boolean chatIsRead; // 읽음 여부
}
