package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.ChatRoom;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // ChatEmpRooms 엔티티를 조인해, 특정 사원 번호에 해당하는 채팅방 리스트를 가져옴
    @Query("SELECT cr FROM ChatRoom cr JOIN ChatEmpRooms cer ON cr = cer.chatRoom WHERE cer.employee.empNo = :empNo")
    List<ChatRoom> findByEmpNo(@Param("empNo") Long empNo);
}