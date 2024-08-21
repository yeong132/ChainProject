package org.zerock.chain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zerock.chain.model.Status;
import org.zerock.chain.model.User;
import org.zerock.chain.repository.ChatRoomRepository;
import org.zerock.chain.repository.ChatUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository ChatUserRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void saveUser(User user) { // 사용자 연결시
        user.setStatus(Status.ONLINE); // 온라인 상태 설정
        ChatUserRepository.save(user); // 저장
    }

    public void disconnect(User user) { // 사용자 연결 끊을 시
        var storedUser = ChatUserRepository.findById(user.getNickName()).orElse(null); // 유저id 확인
        if (storedUser != null) { // 저장된 사용자가 null이 아닌 경우
            storedUser.setStatus(Status.OFFLINE); // 상태를 오프라인으로 설정
            ChatUserRepository.save(storedUser); // 저장
        }
    }

    // 대화 중인 사용자 목록 반환
    public List<User> findActiveChatUsers(String nickname) {
        return chatRoomRepository.findActiveChatUsersByNickname(nickname);
    }
}

