package org.zerock.chain.parkyeongmin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
