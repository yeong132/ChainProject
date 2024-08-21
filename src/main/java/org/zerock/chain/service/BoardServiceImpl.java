package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.BoardDTO;
import org.zerock.chain.dto.BoardRequestDTO;
import org.zerock.chain.model.Board;
import org.zerock.chain.repository.BoardRepository;

import java.util.List;
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

    @Override   // 게시글 수정
    public BoardDTO updateBoard(Long boardNo, BoardRequestDTO boardRequestDTO) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow();

        modelMapper.map(boardRequestDTO, board);
        board = boardRepository.save(board);
        return modelMapper.map(board, BoardDTO.class);
    }

    @Override   // 게시글 삭제
    public void deleteBoard(Long boardNo) {
        boardRepository.deleteById(boardNo);
    }
}
