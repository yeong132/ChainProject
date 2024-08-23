package org.zerock.chain.ksh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.chain.ksh.model.ChatRoom;
import org.zerock.chain.ksh.repository.ChatRoomRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    // 단일 ChatRoom 객체를 반환하도록 수정
    public Optional<ChatRoom> getChatRoom(Long senderEmpNo, // 채팅방 번호 가져오기
                                          Long recipientEmpNo,
                                          boolean createNewRoomIfNotExists // 존재하지 않는 경우 새 방 만들기 호출
    ) {
        return chatRoomRepository
                .findBySenderEmpNoAndRecipientEmpNo(senderEmpNo, recipientEmpNo) // 수신자 발신자로 검색
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        return Optional.of(createChatRoom(senderEmpNo, recipientEmpNo)); // 채팅방을 생성
                    }
                    return Optional.empty();
                });
    }

    // 안 읽은 메시지 카운트 초기화
    public void resetUnreadCount(Long senderEmpNo, Long recipientEmpNo) {
        var chatRoom = getChatRoom(senderEmpNo, recipientEmpNo, false);
        chatRoom.ifPresent(room -> {
            room.setUnreadCount(0); // unread_count 초기화
            chatRoomRepository.save(room); // DB에 저장
        });
    }

    // 채팅방을 생성하고, ChatRoom 객체를 생성하여 반환
    // ChatRoom 객체에서 chatNo 값을 반환하도록 수정
    public Optional<String> getChatRoomNo(Long senderEmpNo, Long recipientEmpNo, boolean createNewRoomIfNotExists) {
        return getChatRoom(senderEmpNo, recipientEmpNo, createNewRoomIfNotExists).map(ChatRoom::getChatNo);  // ChatRoom 객체에서 chatNo 값을 추출하여 반환
    }

    // 채팅방을 생성하고 ChatRoom 객체를 반환하도록 수정
    private ChatRoom createChatRoom(Long senderEmpNo, Long recipientEmpNo) {
        var chatNo = String.format("%s_%s", senderEmpNo, recipientEmpNo);

        // 보낸 사람과 수신자에 대한 두 개의 ChatRoom 객체 생성
        ChatRoom senderRecipient = ChatRoom // 보낸 사람 = 수신자가 채팅방 개체와 같음
                .builder()
                .chatNo(chatNo)
                .senderEmpNo(senderEmpNo)
                .recipientEmpNo(recipientEmpNo)
                .build();

        ChatRoom recipientSender = ChatRoom // 발신자가 수신자가 되고, 수신자가 발신자가 됨
                .builder()
                .chatNo(chatNo)
                .senderEmpNo(recipientEmpNo)
                .recipientEmpNo(senderEmpNo)
                .build();

        // 생성된 채팅방id를 데이터베이스에 저장
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        // 둘 중 하나의 ChatRoom 객체 반환 (예: senderRecipient)
        return senderRecipient;
    }
}
