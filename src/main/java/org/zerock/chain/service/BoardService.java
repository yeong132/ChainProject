package org.zerock.chain.service;

import org.zerock.chain.dto.BoardDTO;
import org.zerock.chain.dto.BoardRequestDTO;

import java.util.List;

public interface BoardService {

    // 전체 목록 조회
    List<BoardDTO> getAllBoards();
    // 개별 상세 조회
    BoardDTO getBoardById(Long boardNo);
    // 생성
    BoardDTO createBoard(BoardRequestDTO boardRequestDTO);
    // 수정
    BoardDTO updateBoard(Long boardNo, BoardRequestDTO boardRequestDTO);
    // 삭제
    void deleteBoard(Long boardNo);

}
