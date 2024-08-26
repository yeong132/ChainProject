package org.zerock.chain.pse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl extends BaseService<Notification> implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    protected List<Notification> getAllItemsByEmpNo(Long empNo) {
        return notificationRepository.findByEmpNo(empNo);
    }

    @Override
    public List<Notification> getNotificationsByType(Long empNo, String type) {
        return getItemsByEmpNo(empNo, empNoParam -> notificationRepository.findByEmpNoAndNotificationType(empNoParam, type));
    }

    @Override
    public List<Notification> getAllNotifications(Long empNo) {
        return getItemsByEmpNo(empNo, this::getAllItemsByEmpNo);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long empNo) {
        notificationRepository.deleteByEmpNo(empNo);
    }

    @Override
    public void deleteNotification(Long notificationNo) {
        notificationRepository.deleteById(notificationNo);
    }

    @Override
    @Transactional  // 읽음 표시
    public void markAsRead(Long notificationNo) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationNo);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override   // 개별 조회(링크 이동)
    public Notification getNotificationById(Long notificationNo) {
        return notificationRepository.findById(notificationNo)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}
