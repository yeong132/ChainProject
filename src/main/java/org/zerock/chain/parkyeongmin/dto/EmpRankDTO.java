package org.zerock.chain.parkyeongmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmpRankDTO {
    private Long rankNo;
    private String rankName;
}
