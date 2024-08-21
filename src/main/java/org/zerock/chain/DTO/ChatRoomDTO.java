package org.zerock.chain.dto;

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
    private String senderEmpNo;
    private String recipientEmpNo;
    private int unreadCount;
}
