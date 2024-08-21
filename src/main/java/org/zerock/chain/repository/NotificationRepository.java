package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByEmpNoAndNotificationType(int empNo, String notificationType); // 알림 타입별 조회
    List<Notification> findByEmpNo(int empNo); // 사원 번호로 모든 알림 조회
    void deleteByEmpNo(int empNo);  // 사원 번호로 모든 알림 삭제
}
