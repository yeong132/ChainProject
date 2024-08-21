package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.ChatRoom;
import org.zerock.chain.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    Optional<ChatRoom>  findBySenderEmpNoAndRecipientEmpNo(String senderEmpNo, String recipientEmpNo);

    // 사용자와 대화 중인 다른 사용자 목록
    @Query("SELECT u FROM User u WHERE u.nickName IN (SELECT cr.recipientEmpNo FROM ChatRoom cr WHERE cr.senderEmpNo = :nickname)")
    List<User> findActiveChatUsersByNickname(@Param("nickname") String nickname);
}