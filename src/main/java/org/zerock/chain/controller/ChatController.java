package org.zerock.chain.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.chain.dto.ChatMessageDTO;
import org.zerock.chain.dto.ChatRoomDTO;
import org.zerock.chain.dto.EmployeeDTO;
import org.zerock.chain.service.ChatService;
import org.zerock.chain.service.EmployeeService;

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

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(ChatMessageDTO message) {
        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDTO addUser(ChatMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getEmpNo()); // 나중에 이름으로 변경?
        return message;
    }

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms/{empNo}")
    public List<ChatRoomDTO> getChatRooms(@PathVariable Long empNo) {
        return chatService.getChatRoomsByEmpNo(empNo);
    }

    @GetMapping("/messages/{chatRoomNo}")
    public List<ChatMessageDTO> getMessages(@PathVariable Long chatRoomNo) {
        return chatService.getMessagesByChatRoomNo(chatRoomNo);
    }

    // 입장, 퇴장, 채팅에 관한 메소드들
    // 채팅: 이름과 보낸 시각이 같이 출력

//    // js 파일: connect를 맺을 때 /app/enter로 메시지 전송
//    @MessageMapping("/enter")
//    @SendTo("/topic/greetings")
//    public Greeting enter(HelloMessage message, StompHeaderAccessor session) throws Exception {
//        return new Greeting(HtmlUtils.htmlEscape(session.getSessionAttributes().get("name") + "님이 입장하였습니다."));
//    }
//
//    // connect를 끊을 때 /app/exit로 메시지 전송, Spring WebSocket에서 특정 경로에 대해 메시지를 매핑하기 위해 사용
//    @MessageMapping("/exit") // /exit 경로로 들어오는 메시지를 처리하는 메서드
//    @SendTo("/topic/greetings") // 반환되는 메시지를 /topic/greetings 경로로 전송하도록 지정 / 이 메서드의 반환 값이 /topic/greetings 구독자들에게 전송, 주로 브로드캐스트 용도로 사용
//    // HelloMessage 타입의 메시지와 StompHeaderAccessor 타입의 세션 정보를 매개변수로 받는 메서드입니다.
//    // 반환 타입은 Greeting입니다.
//    public Greeting exit(HelloMessage message, StompHeaderAccessor session) throws Exception {
//
//        // 세션에서 'name' 속성을 가져와서 HTML 이스케이프 처리된 문자열을 반환합니다.
//        // session에서 세션 속성으로 저장된 name을 가져와서, 해당 사용자가 나갔다는 메시지를 생성합니다.
//        // HtmlUtils.htmlEscape 메서드를 사용하여 HTML 이스케이프 처리를 통해 XSS 공격을 방지합니다.
//        // 생성된 메시지를 Greeting 객체로 반환합니다. 이 반환 값은 /topic/greetings 경로로 전송됩니다.
//        return new Greeting(HtmlUtils.htmlEscape(session.getSessionAttributes().get("name") + "님이 나갔습니다."));
//        // 사원이름을 들고 와야 함
//        // ChatEmpRoomsDTO를 통해서 부서명+사원이름+직책 을 불러와야함?
//
//    }
//
//    // 채팅을 보낼 때 /app/chat로 메시지 전송되도록 구현
//    @MessageMapping("/chat")
//    @SendTo("/topic/greetings") // chatRoomNO 값?
//    public Greeting chat(HelloMessage message, StompHeaderAccessor session) throws Exception {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        Date now = new Date();
//
//        String currentTime = format.format(now);
//
//        System.out.println(currentTime);
//        System.out.println(message.getName());
//
//        return new Greeting(HtmlUtils.htmlEscape(session.getSessionAttributes().get("name") + " : "+message.getName()+"["+currentTime+"]"));
//    }
}
/*
    1. 사용자는 /gs-guide-websocket 경로로 connection 을 맺음
    2. /app/hello 경로로 메시지를 보냄
    3. 컨트롤러가 요청을 받아서 greeting 메소드가 실행됨
    4. 메시지를 가공해서 /topic/greetings 를 구독 중인 사용자들에게 전달함
 */