package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.chain.dto.ChatMessageDTO;
import org.zerock.chain.dto.ChatRoomDTO;
import org.zerock.chain.service.ChatService;

import java.util.List;
import java.util.Map;

// RequestMapping 어노테이션과 상당히 유사
// "/hello" 경로로 메시지가 날아오면 greeting 메소드가 실행되어 Greeting 객체가 반환됨
// Greeting 객체는 @SendTo 어노테이션에 매핑되어있는 "/topic/greeings"를 구독하고 있는 모든 구독자들에게 메시지를 전달한다.
@Log4j2
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private Long empCounter = 1000L; // 초기 empNo 설정

    // 메시지 전송 및 저장
    // 전송된 메시지 처리: 메시지를 수신하면 /topic/public로 브로드캐스트
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(ChatMessageDTO message) {
        // 새로운 메시지 전송 시에는 읽음 상태를 false로 설정
        message.setChatIsRead(false);
        // 메시지를 데이터베이스에 저장
        return chatService.saveMessage(message);
    }

    // 메시지를 읽었을 때 처리
    @MessageMapping("/chat.readMessage")
    public void readMessage(ChatMessageDTO message) {
        // 메시지를 읽은 상태로 업데이트
        chatService.markAsRead(message.getChatNo());
    }

    // 사용자가 처음 접속할 때 empNo를 할당
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDTO addUser(ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        synchronized (this) {
            message.setEmpNo(empCounter);
            empCounter++; // empNo 증가
        }
        headerAccessor.getSessionAttributes().put("empNo", message.getEmpNo());
        return message;
    }

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 특정 사원의 채팅방 목록을 JSON으로 제공
    @GetMapping("/rooms/{empNo}")
    public List<ChatRoomDTO> getChatRooms(@PathVariable Long empNo) {
        return chatService.getChatRoomsByEmpNo(empNo);
    }

    // 채팅방 목록 일반/즐겨찾기 분류된 데이터 반환
    @GetMapping("/rooms/grouped/{empNo}")
    public Map<String, List<ChatRoomDTO>> getChatRoomsGroupedByRoomType(@PathVariable Long empNo) {
        return chatService.getChatRoomsByEmpNoGroupedByRoomType(empNo);
    }

    // 특정 채팅방의 메시지들을 JSON으로 제공
    @GetMapping("/messages/{chatRoomNo}")
    public List<ChatMessageDTO> getMessages(@PathVariable Long chatRoomNo) {
        return chatService.getMessagesByChatRoomNo(chatRoomNo);
    }

}