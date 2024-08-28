package org.zerock.chain.ksh.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.chain.ksh.model.ChatMessage;
import org.zerock.chain.ksh.model.ChatNotification;
import org.zerock.chain.imjongha.model.Employee;
import org.zerock.chain.ksh.repository.ChatMessageRepository;
import org.zerock.chain.ksh.repository.ChatNotificationRepository;
import org.zerock.chain.ksh.repository.ChatRoomRepository;

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
                // 채팅방을 가져옴 (createNewRoomIfNotExists를 true로 설정)
                .getChatRoom(chatMessage.getSenderEmpNo(), chatMessage.getRecipientEmpNo(), true)
                .orElseThrow(() -> new IllegalStateException("채팅방을 생성할 수 없습니다.")); // 예외 처리

        chatMessage.setChatNo(chatRoom.getChatNo()); // chatNo 설정
        chatMessage.setChatRoom(chatRoom); // chatRoomNo 설정
        chatMessage.setRead(false);  // 메시지 읽음 여부 설정
        ChatMessage savedMessage = messageRepository.save(chatMessage);

        // ChatNotification 생성 및 저장
        ChatNotification notification = ChatNotification.builder()
                .chatMessage(savedMessage)
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
    public List<Employee> findActiveChatUsers(Long empNo) {
        return chatRoomRepository.findActiveChatUsersByEmpNo(empNo);
    }

    // 채팅방 재생성 시 최신 메시지 반환 및 이후 채팅만 저장
    public List<ChatMessage> findChatMessages(Long senderEmpNo, Long recipientEmpNo) {
        var chatRoom = chatRoomService.getChatRoom(senderEmpNo, recipientEmpNo, true);
        return chatRoom
                .filter(room -> !chatRoomService.isChatRoomDeleted(senderEmpNo, recipientEmpNo))
                .map(room -> messageRepository.findByChatNo(room.getChatNo()))
                .orElse(new ArrayList<>());
    }

    // 로그인 시 읽지 않은 메시지 불러오기
    public List<ChatMessage> getUnreadMessages(Long recipientEmpNo) {
        return messageRepository.findByRecipientEmpNoAndIsReadFalse(recipientEmpNo);
    }

    // 메시지 읽음 처리 & 메시지 수 초기화
    @Transactional
    public void markMessagesAsRead(Long senderEmpNo, Long recipientEmpNo) {
        var chatRoom = chatRoomService.getChatRoom(senderEmpNo, recipientEmpNo, false);

        chatRoom.ifPresent(room -> {
            messageRepository.markMessagesAsRead(room.getChatNo());  // chatNo로 읽음 처리
            room.setUnreadCount(0);  // unread_count 초기화
            chatRoomRepository.save(room);  // DB에 저장
        });
    }
}
