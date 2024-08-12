package org.zerock.chain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
//import org.zerock.chatting2.handler.ChannelInboundInterceptor;

/*
    WebSocketConfig
    - handler를 이용해 webSocket을 활성화하기 위한 config 파일
    - @EnableWebSocket을 작성하여 활성화
    - endpoint 작성 /ws/chat
    - CORS: setAllowedOrigins("*") 작성
 */

//@Configuration
// 웹소켓 통신에 대한 설정 파일임을 명시, 웹소켓 관련 설정 자동으로 해줌
// WebSocketConfigurer을 implement + override 하여 메서드를 customize 할 수 있는 것
//@EnableWebSocket
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketConfigurer {
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker //웹 소켓 메시지를 다룰 수 있게 허용(STOMP를 사용하기 위한 어노테이션)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Autowired
//    private final WebSocketHandler chatHandler;
//
//    // "/websocket-test" 경로가 곧 연결을 맺는 경로
//    // 정확한 경로는 "ws://localhost:8080/websocket-test"가 될 것이다
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(chatHandler, "/websocket-test").setAllowedOrigins("*");
    // setAllowedOrigins(): 허용할 uri를 지정(default는 same-origin만 허용)
    // 192.168.10.* ?
//    }

//    private final ChannelInboundInterceptor channelInboundInterceptor;

    // 메시지 브로커 구성을 설정하는 메서드
    //  enableSimpleBroker(), setApplicationDestinationPrefixes()  메서드를 사용
    @Override
    public void configureMessageBroker(MessageBrokerRegistry  registry) {
        // /topic 경로로 메시지 전송 -> 브로커 -> 구독자에게 발송
        // 사용자가 메시지를 보낼 때 경로가 "/topic"으로 시작하면 그 메시지를 그대로 구독자들에게 전달한다.
        registry.enableSimpleBroker("/topic"); //발행자가 "/topic"의 경로로 메시지를 주면 구독자들에게 전달

        // /app 경로로 메시지 전송 -> @MessageMapping 어노테이션이 붙은 곳
        // 경로가 "/app"으로 시작하면 중간에 가공을 거쳐 메시지를 전달한다.
        registry.setApplicationDestinationPrefixes("/app"); // 발행자가 "/app"의 경로로 메시지를 주면 가공을 해서 구독자들에게 전달
    }

    // STOMP 프로토콜을 사용하는 WebSocket 엔드포인트를 등록하는 메서드
    // connection을 맺을 때의 경로("/gs-guide-websocket")로 webSocket 연결을 가능하게 함
    // setAllowedOrigins("*"): 모든 오리진에서의 접근을 허용함
    // withSockJS(): 모든 브라우저에서 webSocket 기능 사용(앱은 x)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chatting/ws").withSockJS(); // 커넥션을 맺는 경로 설정. 만약 WebSocket을 사용할 수 없는 브라우저라면 다른 방식을 사용하도록 설정
        // registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*"); // api 통신 시, withSockJS() 설정을 빼야됨
    }

    // 클라이언트의 인바운드 채널에 대한 설정을 구성하는 메서드
    //  interceptors(): stompHandler을 등록해 클라이언트의 webSocket 연결 이전에 처리 작업을 수행할 수 있도록 함
    // ChannelInterceptor를 메시지 정보를 확인하는 용도로 구현
    // connection을 맺을 때 header에 유저 정보를 넣어줄 것이고 메시지를 보낼 때에는 header를 넣어주지 않을 것
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(channelInboundInterceptor);
//    }
}

/*
    1. "/topic"으로 시작하는 경로는 메시지 브로커를 향하도록 설정한다.
    2. "/app"으로 시작하는 경로는 @MessageMapping을 향하도록 설정한다.
    3. 구독자는 "/topic/greetings"으로 시작하는 경로를 구독한다. (1번 설정 때문에)
    4.발행자는 "/app/hello"로 시작하는 경로로 메시지를 보낸다.
    5. 2번 설정 때문에 @MessageMapping("/hello")가 붙어있는 곳으로 메시지가 간다.
    6. 메시지 가공이 끝난 후 ("/topic/greetings")로 보낸다.
    7. 1번 설정 때문에 이 메시지는 메시지 브로커로 가게 된다.
    8. 메시지 브로커에서 "/topic/greetings"를 구독 중인 구독자들에게 메시지를 전송한다.
   */