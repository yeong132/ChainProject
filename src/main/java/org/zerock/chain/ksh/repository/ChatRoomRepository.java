package org.zerock.chain.ksh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.ksh.model.ChatRoom;
import org.zerock.chain.imjongha.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findBySenderEmpNoAndRecipientEmpNo(Long senderEmpNo, Long recipientEmpNo);

    // 사용자와 대화 중인 다른 사용자 목록
    @Query("SELECT u FROM Employee u WHERE u.empNo IN (SELECT cr.recipientEmpNo FROM ChatRoom cr WHERE cr.senderEmpNo = :empNo)")
    List<Employee> findActiveChatUsersByEmpNo(@Param("empNo") Long empNo);
}