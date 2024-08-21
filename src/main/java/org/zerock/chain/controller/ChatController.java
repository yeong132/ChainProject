package org.zerock.chain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.chain.model.ChatMessage;
import org.zerock.chain.model.ChatNotification;
import org.zerock.chain.service.ChatMessageService;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);

        // 클라이언트로 알림 전송
        messagingTemplate.convertAndSendToUser(
                savedMsg.getRecipientEmpNo(), "/queue/messages",
                ChatNotification.builder() // 채팅 알림
                        .chatMessage(savedMsg)
                        .senderEmpNo(savedMsg.getSenderEmpNo())
                        .recipientEmpNo(savedMsg.getRecipientEmpNo())
                        .chatContent(savedMsg.getChatContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderEmpNo}/{recipientEmpNo}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderEmpNo,
                                                              @PathVariable String recipientEmpNo) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderEmpNo, recipientEmpNo));
    }
    // 로그인 시 읽지 않은 메시지 불러오기
    @GetMapping("/messages/unread")
    public ResponseEntity<List<ChatMessage>> getUnreadMessages(@RequestParam String recipientEmpNo) {
        // unread 메시지 검색 로직 구현
        List<ChatMessage> unreadMessages = chatMessageService.getUnreadMessages(recipientEmpNo);
        return ResponseEntity.ok(unreadMessages);
    }
}