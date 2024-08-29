package org.zerock.chain.pse.service;

import org.zerock.chain.pse.model.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotificationsByType(Long empNo, String notificationType); // 알림 타입별 조회
    List<Notification> getAllNotifications(Long empNo); // 사원 번호로 모든 알림 조회
    void deleteAllNotifications(Long empNo);  // 사원 번호로 모든 알림 삭제
    void deleteNotification(Long notificationNo);   // 알림 번호로 개별 삭제
    void markAsRead(Long notificationNo);  // 알림을 읽음으로 표시
    Notification getNotificationById(Long notificationNo);
    void updateNotificationSettingByType(Long empNo, String notificationType, Boolean enabled);  // 알림 온오프
    void deleteReadNotifications(Long empNo); // 특정 사원의 읽은 알림을 모두 삭제함
}
