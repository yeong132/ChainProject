package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldsDTO {
    private int fieldNo;           // 입력란 번호
    private int formNo;            // 결재양식 번호
    private String fieldName;      // 입력란 이름
    private String fieldType;      // 입력란 종류
    private String fieldOptions;   // 입력란 선택옵션
    private String category;       // 양식 종류
}
