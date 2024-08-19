package org.zerock.chain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.model.Notification;
import org.zerock.chain.repository.NotificationRepository;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> getNotificationsByType(int empNo, String type) {
        return notificationRepository.findByEmpNoAndNotificationType(empNo, type);
    }

    @Override
    public List<Notification> getAllNotifications(int empNo) {
        return notificationRepository.findByEmpNo(empNo);
    }

    @Override
    public void deleteAllNotifications(int empNo) {
        notificationRepository.deleteByEmpNo(empNo);
    }

    @Override
    public void deleteNotification(Long notificationNo) {
        notificationRepository.deleteById(notificationNo);
    }
}
