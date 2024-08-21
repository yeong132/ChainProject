package org.zerock.chain.service;

import org.zerock.chain.model.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotificationsByType(int empNo, String notificationType); // 알림 타입별 조회
    List<Notification> getAllNotifications(int empNo); // 사원 번호로 모든 알림 조회
    void deleteAllNotifications(int empNo);  // 사원 번호로 모든 알림 삭제
    void deleteNotification(Long notificationNo);   // 알림 번호로 개별 삭제
}
