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
//@RequestMapping("/api/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        // john/queue/messages

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

//    private final ChatService chatService;
//    private Long empCounter = 1000L; // 초기 empNo 설정
//
//    // 메시지 전송 및 저장
//    // 전송된 메시지 처리: 메시지를 수신하면 /topic/public로 브로드캐스트
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
//    public ChatMessageDTO sendMessage(ChatMessageDTO message) {
//        // 새로운 메시지 전송 시에는 읽음 상태를 false로 설정
//        message.setChatIsRead(false);
//        // 메시지를 데이터베이스에 저장
//        return chatService.saveMessage(message);
//    }
//
//    // 메시지를 읽었을 때 처리
//    @MessageMapping("/chat.readMessage")
//    public void readMessage(ChatMessageDTO message) {
//        // 메시지를 읽은 상태로 업데이트
//        chatService.markAsRead(message.getChatNo());
//    }
//
//    // 사용자가 처음 접속할 때 empNo를 할당
//    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/public")
//    public ChatMessageDTO addUser(ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
//        synchronized (this) {
//            message.setEmpNo(empCounter);
//            empCounter++; // empNo 증가
//        }
//        headerAccessor.getSessionAttributes().put("empNo", message.getEmpNo());
//        return message;
//    }
//
//    public ChatController(ChatService chatService) {
//        this.chatService = chatService;
//    }
//
//    // 특정 사원의 채팅방 목록을 JSON으로 제공
//    @GetMapping("/rooms/{empNo}")
//    public List<ChatRoomDTO> getChatRooms(@PathVariable Long empNo) {
//        return chatService.getChatRoomsByEmpNo(empNo);
//    }
//
//    // 채팅방 목록 일반/즐겨찾기 분류된 데이터 반환
//    @GetMapping("/rooms/grouped/{empNo}")
//    public Map<String, List<ChatRoomDTO>> getChatRoomsGroupedByRoomType(@PathVariable Long empNo) {
//        return chatService.getChatRoomsByEmpNoGroupedByRoomType(empNo);
//    }
//
//    // 특정 채팅방의 메시지들을 JSON으로 제공
//    @GetMapping("/messages/{chatRoomNo}")
//    public List<ChatMessageDTO> getMessages(@PathVariable Long chatRoomNo) {
//        return chatService.getMessagesByChatRoomNo(chatRoomNo);
//    }

}