package org.zerock.chain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.chain.model.ChatMessage;
import org.zerock.chain.model.ChatNotification;
import org.zerock.chain.model.User;
import org.zerock.chain.repository.ChatMessageRepository;
import org.zerock.chain.repository.ChatNotificationRepository;
import org.zerock.chain.repository.ChatRoomRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository messageRepository;
    private final ChatNotificationRepository notificationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatRoom = chatRoomService
                .getChatRoom(chatMessage.getSenderEmpNo(), chatMessage.getRecipientEmpNo(), true)  // 채팅방을 가져옴 (createNewRoomIfNotExists를 true로 설정)
                .orElseThrow(() -> new IllegalStateException("채팅방을 생성할 수 없습니다.")); // 예외 처리 하기

        chatMessage.setChatNo(chatRoom.getChatNo()); // chatNo 설정
        chatMessage.setChatRoom(chatRoom); // chatRoomNo 설정
        chatMessage.setRead(false);  // 메시지 읽음 여부 설정
        ChatMessage savedMessage = messageRepository.save(chatMessage);

        // ChatNotification 생성 및 저장
        ChatNotification notification = ChatNotification.builder()
                .chatMessage(savedMessage)  // 참조된 chat_message 객체를 설정
                .senderEmpNo(savedMessage.getSenderEmpNo())
                .recipientEmpNo(savedMessage.getRecipientEmpNo())
                .chatContent(savedMessage.getChatContent())
                .build();
        notificationRepository.save(notification);

        // unread_count 증가
        chatRoom.setUnreadCount(chatRoom.getUnreadCount() + 1);
        chatRoomRepository.save(chatRoom);

        return savedMessage;
    }

    // 현재 사용자의 대화 중인 사용자 목록 반환
    public List<User> findActiveChatUsers(String nickname) {
        return chatRoomRepository.findActiveChatUsersByNickname(nickname);
    }

    public List<ChatMessage> findChatMessages(String senderEmpNo, String recipientEmpNo) {
        var chatNo = chatRoomService.getChatRoomNo(
                senderEmpNo,
                recipientEmpNo,
                false); // 방이 없을 경우 false
        return chatNo.map(messageRepository::findByChatNo).orElse(new ArrayList<>());
    }

    // 로그인 시 읽지 않은 메시지 불러오기
    public List<ChatMessage> getUnreadMessages(String recipientEmpNo) {
        return messageRepository.findByRecipientEmpNoAndIsReadFalse(recipientEmpNo);
    }

    // 메시지 읽음 처리 & 메시지 수 초기화
    @Transactional
    public void markMessagesAsRead(String senderEmpNo, String recipientEmpNo) {
        var chatRoom = chatRoomService.getChatRoom(senderEmpNo, recipientEmpNo, false);

        chatRoom.ifPresent(room -> {
            messageRepository.markMessagesAsRead(room.getChatNo());  // chatNo로 읽음 처리
            room.setUnreadCount(0);  // unread_count 초기화
            chatRoomRepository.save(room);  // DB에 저장
        });
    }
}
