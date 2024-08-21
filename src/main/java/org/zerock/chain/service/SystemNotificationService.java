package org.zerock.chain.service;

import org.zerock.chain.model.SystemNotification;

import java.util.List;

public interface SystemNotificationService {
    void saveSystemNotification(SystemNotification systemNotification);
    List<SystemNotification> getAllSystemNotifications();
    void deleteAllSystemNotifications();
    void deleteSystemNotification(Long systemNo);
}
