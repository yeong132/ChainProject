package org.zerock.chain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker //웹 소켓 메시지를 다룰 수 있게 허용(STOMP를 사용하기 위한 어노테이션)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 구성을 설정하는 메서드
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /topic 경로로 메시지 전송 -> 브로커 -> 구독자에게 발송
        // 사용자가 메시지를 보낼 때 경로가 "/topic"으로 시작하면 그 메시지를 그대로 구독자들에게 전달
        registry.enableSimpleBroker("/topic");
        // /app 경로로 메시지 전송 -> @MessageMapping 어노테이션이 붙은 곳
        // 경로가 "/app"으로 시작하면 중간에 가공을 거쳐 메시지를 전달
        registry.setApplicationDestinationPrefixes("/app");
        //
        registry.setUserDestinationPrefix("/topic");
    }

    // STOMP 프로토콜을 사용하는 WebSocket 엔드포인트를 등록하는 메서드
    // connection을 맺을 때의 경로("/ws")로 webSocket 연결을 가능하게 함
    // setAllowedOrigins("*"): 허용할 uri를 지정, 모든 오리진에서의 접근을 허용함
    // withSockJS(): 모든 브라우저에서 webSocket 기능 사용(앱은 x), api 통신 시 withSockJS() 설정을 빼야됨?
    // 정확한 경로는 "ws://localhost:8080/ws"
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}
