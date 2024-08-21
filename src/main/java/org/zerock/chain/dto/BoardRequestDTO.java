package org.zerock.chain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BoardRequestDTO {

    private Long boardNo;
    private String boardCategory;
    private String boardName;
    private String boardContent;
    private String boardLocation;
    private String boardFiles;
    private String boardAuthor;
    private LocalDate boardUploadDate;


}
