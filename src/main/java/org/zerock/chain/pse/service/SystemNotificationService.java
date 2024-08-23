package org.zerock.chain.pse.service;

import org.zerock.chain.pse.model.SystemNotification;

import java.util.List;

public interface SystemNotificationService {
    void saveSystemNotification(SystemNotification systemNotification);
    List<SystemNotification> getAllSystemNotifications();
    void deleteAllSystemNotifications();
    void deleteSystemNotification(Long systemNo);
}
