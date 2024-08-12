package org.zerock.chain.service;

import org.zerock.chain.dto.CommentDTO;
import org.zerock.chain.dto.CommentRequestDTO;

import java.util.List;

public interface CommentService {

    List<CommentDTO> getCommentsByQnaNo(Long qnaNo); // 문의글 번호로 댓글 조회
    CommentDTO addComment(CommentRequestDTO commentRequestDTO); // 댓글 추가
    void deleteComment(Long commentNo); // 댓글 삭제
    void deleteCommentsByQnaNo(Long qnaNo); // 특정 QnA 번호에 해당하는 모든 댓글 삭제
    CommentDTO updateComment(Long commentNo, CommentRequestDTO commentRequestDTO); // 수정
}
