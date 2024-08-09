package org.zerock.chain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QnaRequestDTO {

    private String qnaName;
    private String qnaAuthor;
    private String qnaContent;
    private String qnaCategory;
    private boolean qnaStatus; //  답변여부
    private String qnaFiles;

}