package org.zerock.chain.pse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.pse.model.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByEmpNoAndNotificationType(int empNo, String notificationType); // 알림 타입별 조회
    List<Notification> findByEmpNo(int empNo); // 사원 번호로 모든 알림 조회
    void deleteByEmpNo(int empNo);  // 사원 번호로 모든 알림 삭제
    Optional<Notification> findById(Long notificationNo); // 알림 번호로 개별 조회
}
