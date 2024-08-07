package org.zerock.chain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpRankMappingDTO {

    private int id; // 매핑 테이블의 기본 키
    private int emp_no; // 사원 번호
    private int rank_no; // 직급 번호
}
