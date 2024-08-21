package org.zerock.chain.dto;

import lombok.Data;

import java.util.List;

@Data
public class FormFieldsDTOList {
    private List<FormFieldsDTO> formFields;  // 입력란 정보 리스트
}
