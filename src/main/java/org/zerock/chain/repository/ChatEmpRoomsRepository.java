package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.ChatEmpRooms;

@Repository
public interface ChatEmpRoomsRepository extends JpaRepository<ChatEmpRooms, Long> {
    // 사원-채팅방 맵핑 관련 쿼리 메소드
}