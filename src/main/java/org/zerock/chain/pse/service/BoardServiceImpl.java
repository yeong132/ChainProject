package org.zerock.chain.pse.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.pse.dto.BoardDTO;
import org.zerock.chain.pse.dto.BoardRequestDTO;
import org.zerock.chain.pse.model.Board;
import org.zerock.chain.pse.repository.BoardRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl  implements  BoardService{

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    public BoardServiceImpl(ModelMapper modelMapper, BoardRepository boardRepository) {
        this.modelMapper = modelMapper;
        this.boardRepository = boardRepository;
    }

    @Override   // 전체 목록 조회
    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
    }

    @Override   // 개별조회
    public BoardDTO getBoardById(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElseThrow();
        return modelMapper.map(board, BoardDTO.class);
    }

    @Override   // 게시글 생성
    public BoardDTO createBoard(BoardRequestDTO boardRequestDTO) {
        Board board = modelMapper.map(boardRequestDTO, Board.class);
        board = boardRepository.save(board);
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        return boardDTO;
    }

    // 예시: BoardServiceImpl의 updateBoard 메서드에서 로그 추가
    @Override   // 게시글 수정
    public BoardDTO updateBoard(Long boardNo, BoardRequestDTO boardRequestDTO) {
        // 로그 추가
        System.out.println("Original boardFiles: " + boardRequestDTO.getBoardFiles());
        System.out.println("Original boardLocation: " + boardRequestDTO.getBoardLocation());

        // 게시글 조회
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        // 로그 추가
        System.out.println("Before update - boardFiles: " + board.getBoardFiles());
        System.out.println("Before update - boardLocation: " + board.getBoardLocation());

        // boardFiles와 boardLocation을 명시적으로 설정
        if (boardRequestDTO.getBoardFiles() != null && !boardRequestDTO.getBoardFiles().isEmpty()) {
            board.setBoardFiles(boardRequestDTO.getBoardFiles().trim());
        }
        if (boardRequestDTO.getBoardLocation() != null && !boardRequestDTO.getBoardLocation().isEmpty()) {
            board.setBoardLocation(boardRequestDTO.getBoardLocation().trim());
        }

        // 로그 추가
        System.out.println("After update - boardFiles: " + board.getBoardFiles());
        System.out.println("After update - boardLocation: " + board.getBoardLocation());

        // 나머지 필드들은 ModelMapper를 통해 업데이트
        modelMapper.map(boardRequestDTO, board);

        // 변경된 게시글 저장
        board = boardRepository.save(board);

        return modelMapper.map(board, BoardDTO.class);
    }

    @Override   // 게시글 삭제
    public void deleteBoard(Long boardNo) {
        boardRepository.deleteById(boardNo);
    }
}
