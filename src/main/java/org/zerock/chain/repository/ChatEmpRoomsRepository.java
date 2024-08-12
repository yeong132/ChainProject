package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.ChatEmpRooms;
import org.zerock.chain.model.ChatRoom;

import java.util.List;

@Repository
public interface ChatEmpRoomsRepository extends JpaRepository<ChatEmpRooms, Long> {
    // 사원-채팅방 맵핑 관련 쿼리 메소드
    @Query("SELECT cr FROM ChatRoom cr JOIN ChatEmpRooms cer ON cr = cer.chatRoom WHERE cer.employee.empNo = :empNo")
    List<ChatRoom> findByEmpNo(@Param("empNo") Long empNo);
}