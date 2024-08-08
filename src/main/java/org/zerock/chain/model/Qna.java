package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qna")
@ToString
public class Qna {

    /** * 고정 질문답변 테이블 (자주묻는질문번호, 제목, 내용, 생성일)
     * 질문 테이블 (질문번호, 제목, 카테고리, 작성자, 내용, 첨부파일, 작성일 )
     * 답글 테이블 (질문번호, 답글 번호, 답글 제목, 답글 내용, 작성일)*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_no")
    private Long qnaNo; // 질문아이디,번호

    @Column(name = "qna_name")
    private String qnaName; // 제목

    @Column(name = "qna_author")
    private String qnaAuthor; // 작성자

    @Column(name = "qna_content")
    private String qnaContent; // 내용

    @Column(name = "qna_category")
    private String qnaCategory; // 카테고리

    @Column(name = "qna_files")
    private String qnaFiles;    //첨부파일

    @Column(name = "todo_upload_date", updatable = false)
    private LocalDateTime qnaUploadDate;    // 작성일


}
