package org.zerock.chain.ksh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotificationDTO {
    private Long id;
    private Long senderEmpNo;
    private Long recipientEmpNo;
    private String chatContent;
}
