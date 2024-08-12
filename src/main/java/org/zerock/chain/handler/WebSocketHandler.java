package org.zerock.chain.handler;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.zerock.chain.dto.ChatMessageDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TextWebSocketHandler: text message를 처리할 수 있는 handler 클래스
@Log4j2
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    // WebSocketSession을 리스트로 가지고 있는 필드
    // 현재 연결 중인 클라이언트들이 존재
    // 접속하면 리스트에 저장, 접속을 끊으면 리스트에서 제거

    // WebSocketSession : 연결을 맺고 나서 유지되는 세션
    // 연결이 끊기기 전까지 유지됨, 연결을 맺을 때 전달한 정보 사용 가능
    //  ㄴ 연결을 유저 정보를 헤더에 담아서 맺었으면, 이 정보를 계속 사용할 수 있음
    // sendMessage로 메시지 전송
    private List<WebSocketSession> sessionList = new ArrayList<>();

    // 웹소켓 연결 시,
    // 연결을 맺고 나서 실행되는 메소드
    // WebSocketSession의 getHandShakeHeaders 메소드를 실행하면 헤더를 가져올 수 있음
    // 그 헤더 중 key가 name인 value를 가져옴 -> 연결 중인 사용자의 이름을 의미
    // sessionList에 해당 유저의 WebSocketSession을 추가
    //  ㄴ 이 유저가 채팅에 참여하고 있다는 것을 의미, 이 채팅에 참여 중인 다른 유저의 메시지를 받을 수 있음
    // sessionList에 추가하고나면 sessionList에 있는 모든 WebSocketSession에 메시지를 보낸다.
    //  ㄴ 현재 채팅에 참여 중인 모든 사람들에게 입장 메시지를 보냄
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String name = session.getHandshakeHeaders().get("name").get(0);
        sessionList.add(session);
        System.out.println("sessionList = " + sessionList.size());
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(name+"님께서 입장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 데이터 통신 시, (양방향 데이터 통신)
    // 메시지를 다루는 메소드
    // 메시지를 보낼 때 누가 보냈는지와 보낸 시간을 함께 출력해야함
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        Gson gson = new Gson();
        String name = session.getHandshakeHeaders().get("name").get(0);

        sessionList.forEach(s-> {
            try {
                ChatMessageDTO chatMessageDTO = gson.fromJson(message.getPayload(), ChatMessageDTO.class);
                // 메시지를 다음과 같이 변환: minsoo : 안녕하세요~[5월 16일 17시 20분]
                s.sendMessage(new TextMessage(name + " : "+ chatMessageDTO.getChatContent()+"   "+ chatMessageDTO.getChatSentTime()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 웹소켓 연결 종료 시,
    // afterConnectionEstablished 메소드와 거의 동일
    // 채팅에 참여 중인 모든 유저에게 퇴장 메시지를 보냄
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session); // 세션 저장소에서 연결이 끊긴 사용자를 삭제함

        log.info("session = " + sessionList.size());

        String name = session.getHandshakeHeaders().get("name").get(0);

        sessionList.forEach(s-> { // 다른 사용자들에게 퇴장했다고 알림
            try {
                s.sendMessage(new TextMessage(name+"님께서 퇴장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // 웹소켓 통신 에러 시,
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {}
}