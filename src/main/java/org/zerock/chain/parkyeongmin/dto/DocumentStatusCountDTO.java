package org.zerock.chain.parkyeongmin.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentStatusCountDTO {

    private int requestsCount;
    private int inProgressCount;
    private int rejectedCount;
    private int completedCount;
}
