package org.zerock.chain.ksh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.chain.ksh.model.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDTO {
    private String nickName;
    private String fullName;
    private Status status;
}
