package org.zerock.chain.pse.service;

import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.model.SystemNotification;

import java.util.List;

public interface SystemNotificationService {
    void saveSystemNotification(SystemNotification systemNotification);
    List<SystemNotification> getAllSystemNotifications();
    void deleteAllSystemNotifications();
    void deleteSystemNotification(Long systemNo);
    void markAsReadSystem(Long systemNo);  // 알림을 읽음으로 표시
    SystemNotification getSystemNotificationById(Long systemNo); // 개별 조회

}
