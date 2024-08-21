package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.model.ChatMessage;
import org.zerock.chain.model.ChatRoom;
import org.zerock.chain.dto.ChatMessageDTO;
import org.zerock.chain.dto.ChatRoomDTO;
import org.zerock.chain.repository.ChatMessageRepository;
import org.zerock.chain.repository.ChatRoomRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, ModelMapper modelMapper) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.modelMapper = modelMapper;
    }

    // 사원 접속 처리
    public List<ChatRoomDTO> getChatRoomsByEmpNo(Long empNo) {
        // 예: 특정 empNo로 채팅방 리스트를 가져오는 쿼리 실행
        List<ChatRoom> chatRooms = chatRoomRepository.findByEmpNo(empNo);

        // 예외 처리 또는 추가 로직이 있을 수 있음
        // null을 반환할 가능성이 있다면, null 대신 빈 리스트를 반환하도록 null 체크를 해두는 것이 안전합니다.

        // 사원 번호(empNo)에 해당하는 채팅방 조회
        return chatRooms.stream()
                .map(chatRoom -> modelMapper.map(chatRoom, ChatRoomDTO.class))
                .collect(Collectors.toList());
    }

    // 기존 메시지 반환
    public List<ChatMessageDTO> getMessagesByChatRoomNo(Long chatRoomNo) {
        // 메시지 리스트 반환 로직 구현
        // 특정 채팅방 번호(chatRoomNo)로 메시지 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_ChatRoomNo(chatRoomNo);

        // 예외 처리 또는 추가 로직이 있을 수 있음
        // null을 반환할 가능성이 있다면, null 대신 빈 리스트를 반환하도록 null 체크를 해두는 것이 안전합니다.

        // ChatMessage를 ChatMessageDTO로 변환하여 반환
        return messages.stream()
                .map(message -> modelMapper.map(message, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }

    // 메시지를 저장하는 메서드 추가
    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = modelMapper.map(chatMessageDTO, ChatMessage.class);
        chatMessage = chatMessageRepository.save(chatMessage);
        return modelMapper.map(chatMessage, ChatMessageDTO.class);
    }

    // 읽음 상태 업데이트
    public void markAsRead(Long chatNo) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(chatNo);
        messageOpt.ifPresent(message -> {
            message.setChatIsRead(true);
            chatMessageRepository.save(message);
        });
    }

    // 채팅방 목록 일반/즐겨찾기 분류
    public Map<String, List<ChatRoomDTO>> getChatRoomsByEmpNoGroupedByRoomType(Long empNo) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByEmpNo(empNo);

        return chatRooms.stream()
                .collect(Collectors.groupingBy(
                        chatRoom -> chatRoom.getRoomType() ? "favorites" : "common",
                        Collectors.mapping(
                                chatRoom -> modelMapper.map(chatRoom, ChatRoomDTO.class),
                                Collectors.toList()
                        )
                ));
    }
}
