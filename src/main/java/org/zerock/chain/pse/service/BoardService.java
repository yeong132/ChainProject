package org.zerock.chain.pse.service;

import org.zerock.chain.pse.dto.BoardDTO;
import org.zerock.chain.pse.dto.BoardRequestDTO;

import java.util.List;

public interface BoardService {

    // 전체 목록 조회
    List<BoardDTO> getAllBoards();
    // 개별 상세 조회
    BoardDTO getBoardById(Long boardNo);
    // 게시글 생성
    BoardDTO createBoard(BoardRequestDTO boardRequestDTO);
    // 게시글 수정
    BoardDTO updateBoard(Long boardNo, BoardRequestDTO boardRequestDTO);
    // 게시글 삭제
    void deleteBoard(Long boardNo);

}
