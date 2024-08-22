package org.zerock.chain.ksh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String chatNo;
    private Long senderEmpNo;
    private Long recipientEmpNo;
    private int unreadCount;
}
