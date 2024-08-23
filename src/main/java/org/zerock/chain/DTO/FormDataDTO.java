package org.zerock.chain.dto;

import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDataDTO {
    private int docNo;
    private int fieldNo;
    private String fieldValue;
}
