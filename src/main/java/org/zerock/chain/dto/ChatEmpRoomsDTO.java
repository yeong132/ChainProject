package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 사원 - 채팅방 매핑 관리*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatEmpRoomsDTO {
    private Long chatRoomNo; // 채팅방 번호
    private Long empNo; // 참여 사원 번호
}
