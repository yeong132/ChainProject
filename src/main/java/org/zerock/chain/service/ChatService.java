package org.zerock.chain.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.model.ChatMessage;
import org.zerock.chain.model.ChatRoom;
import org.zerock.chain.dto.ChatMessageDTO;
import org.zerock.chain.dto.ChatRoomDTO;
import org.zerock.chain.model.ChatMessage;
import org.zerock.chain.repository.ChatMessageRepository;
import org.zerock.chain.repository.ChatRoomRepository;

import java.util.List;
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

    public List<ChatRoomDTO> getChatRoomsByEmpNo(Long empNo) {
        // 채팅방 리스트 반환 로직 구현
        // 예: 특정 empNo로 채팅방 리스트를 가져오는 쿼리 실행
        List<ChatRoom> chatRooms = chatRoomRepository.findByEmpNo(empNo);

        // 예외 처리 또는 추가 로직이 있을 수 있음
        // null을 반환할 가능성이 있다면, null 대신 빈 리스트를 반환하도록 null 체크를 해두는 것이 안전합니다.

        // 사원 번호에 해당하는 채팅방 리스트 반환
        return chatRooms.stream()
                .map(chatRoom -> modelMapper.map(chatRoom, ChatRoomDTO.class))
                .collect(Collectors.toList());
    }

    public List<ChatMessageDTO> getMessagesByChatRoomNo(Long chatRoomNo) {
        // 메시지 리스트 반환 로직 구현
        // 예: 특정 chatRoomNo로 메시지 리스트를 가져오는 쿼리 실행
        List<ChatMessage> messages = chatMessageRepository.findByChatRoom_ChatRoomNo(chatRoomNo);

        // 예외 처리 또는 추가 로직이 있을 수 있음
        // null을 반환할 가능성이 있다면, null 대신 빈 리스트를 반환하도록 null 체크를 해두는 것이 안전합니다.

        // ChatMessage를 ChatMessageDTO로 변환하여 반환
        return messages.stream()
                .map(message -> modelMapper.map(message, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }
}
