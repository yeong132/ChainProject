package org.zerock.chain.dto;

import lombok.Data;

/* 사원 - 채팅방 매핑 관리*/
@Data
public class ChatEmpRoomsDTO {
    private Long chatRoomNo; // 채팅방 번호
    private Long empNo; // 참여 사원 번호
}
