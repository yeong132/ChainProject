package org.zerock.chain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.chain.model.User;
import org.zerock.chain.service.ChatUserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserService chatUserService;

    @MessageMapping("/user.addUser")
    @SendTo("/topic/public")
    public User addUser(@Payload User user) {
        chatUserService.saveUser(user); // 사용자 저장 후,
        return user; // 받은 사용자 반환
    }

    @MessageMapping("/user.disconnectUser") // 연결 끊김
    @SendTo("/topic/public")
    public User disconnectUser(@Payload User user) {
        chatUserService.disconnect(user);
        return user;
    }

    // 대화 중인 사용자만 반환(채팅방 호출)
    @GetMapping("/chat/activeUsers")
    public ResponseEntity<List<User>> findActiveChatUsers(@RequestParam String nickname) {
        List<User> activeUsers = chatUserService.findActiveChatUsers(nickname);
        return ResponseEntity.ok(activeUsers);
    }
}
