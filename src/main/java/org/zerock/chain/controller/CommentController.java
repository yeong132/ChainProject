package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.CommentDTO;
import org.zerock.chain.dto.CommentRequestDTO;
import org.zerock.chain.service.CommentService;

import java.util.List;

@Controller
@RequestMapping("/comments")
@Log4j2
public class CommentController {

    private final CommentService commentService;

    // 생성자 주입 방식으로 변경
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 특정 문의글 번호에 해당하는 모든 댓글을 가져오는 메서드
    @GetMapping("/qna/{qnaNo}")
    public String getComments(@PathVariable Long qnaNo, Model model) {
        List<CommentDTO> comments = commentService.getCommentsByQnaNo(qnaNo);
        model.addAttribute("comments", comments);
        model.addAttribute("qnaNo", qnaNo);
        return "user/qaDetail"; // Q&A 상세 페이지로 이동하며 댓글 리스트를 전달
    }

    // 특정 문의글에 새로운 댓글을 추가하는 메서드
    @PostMapping("/qna/{qnaNo}/add")
    public String addComment(@PathVariable Long qnaNo, @ModelAttribute CommentRequestDTO commentRequestDTO) {
        commentRequestDTO.setQnaNo(qnaNo);
        try {
            commentService.addComment(commentRequestDTO);
            log.info("New comment added successfully for QnA ID: {}", qnaNo);
        } catch (Exception e) {
            log.error("Error adding comment for QnA ID: {}", qnaNo, e);
            return "redirect:/error"; // 에러 페이지로 리다이렉트
        }

        return "redirect:/user/qna/detail/" + qnaNo;
    }

    // 댓글 수정 메서드
    @PutMapping("/edit/{commentNo}")
    public String editComment(@PathVariable Long commentNo, @ModelAttribute CommentRequestDTO commentRequestDTO, @RequestParam Long qnaNo) {
        try {
            commentService.updateComment(commentNo, commentRequestDTO);
            log.info("Comment updated successfully: {}", commentNo);
        } catch (Exception e) {
            log.error("Error updating comment: {}", commentNo, e);
            return "redirect:/comments/qna/detail/" + qnaNo + "?error=true";
        }
        return "redirect:/comments/qna/detail/" + qnaNo;
    }




    // 특정 댓글을 삭제하는 메서드
    @DeleteMapping("/delete/{commentNo}")
    public String deleteComment(@PathVariable Long commentNo, @RequestParam Long qnaNo) {
        try {
            commentService.deleteComment(commentNo);
            log.info("Comment deleted successfully: {}", commentNo);
        } catch (Exception e) {
            log.error("Error deleting comment: {}", commentNo, e);
            return "redirect:/comments/qna/detail/" + qnaNo + "?error=true";
        }
        return "redirect:/comments/qna/detail/" + qnaNo;
    }



}
