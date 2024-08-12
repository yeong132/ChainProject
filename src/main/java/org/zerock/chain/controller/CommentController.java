package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.chain.dto.CommentDTO;
import org.zerock.chain.dto.CommentRequestDTO;
import org.zerock.chain.service.CommentService;
import org.zerock.chain.service.QnaService;

import java.util.List;

@Controller
@RequestMapping("/comment")
@Log4j2
public class CommentController {


    private final CommentService commentService;
    private final QnaService qnaService;

    // 생성자 주입 방식으로 변경
    public CommentController(CommentService commentService, QnaService qnaService) {
        this.commentService = commentService;
        this.qnaService = qnaService;
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

            // 댓글 추가 후 QnA 상태를 true로 업데이트
            qnaService.updateQnaStatus(qnaNo, true);
        } catch (Exception e) {
            log.error("Error adding comment for QnA ID: {}", qnaNo, e);
            return "redirect:/error"; // 에러 페이지로 리다이렉트
        }

        return "redirect:/user/qna/detail/" + qnaNo;
    }

    // 댓글 수정 메서드
    @PostMapping("/edit/{commentNo}")
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
    @PostMapping("/delete/{commentNo}")
    public String deleteComment(@PathVariable("commentNo") Long commentNo, @RequestParam Long qnaNo) {
        commentService.deleteComment(commentNo);

        // 댓글 삭제 후 해당 QnA의 댓글 여부를 0으로 설정
        qnaService.updateQnaStatus(qnaNo, false);

        return "redirect:/user/qna/detail/" + qnaNo;
    }


}
