package org.zerock.chain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitRequest {
    private String docTitle;
    private List<FormFieldsDTO> formFields;
    private Map<Integer, String> formData;
    private String file; // 양식 선택값을 위한 필드
}
