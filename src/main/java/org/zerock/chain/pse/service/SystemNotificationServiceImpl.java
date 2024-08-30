package org.zerock.chain.pse.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.model.SystemNotification;
import org.zerock.chain.pse.repository.SystemNotificationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SystemNotificationServiceImpl implements SystemNotificationService {

    private final SystemNotificationRepository systemNotificationRepository;

    @Autowired
    public SystemNotificationServiceImpl(SystemNotificationRepository systemNotificationRepository) {
        this.systemNotificationRepository = systemNotificationRepository;
    }

    @Override   // 시스템 알림 등록
    public void saveSystemNotification(SystemNotification systemNotification) {
        systemNotificationRepository.save(systemNotification);
    }

    @Override   // 모든 시스템 알림 조회
    public List<SystemNotification> getAllSystemNotifications() {
        return systemNotificationRepository.findAll();
    }

    @Override // 모든 시스템 알림 삭제
    @Transactional
    public void deleteAllSystemNotifications() {
        systemNotificationRepository.deleteAll();
    }

    @Override // 개별 시스템 알림 삭제
    public void deleteSystemNotification(Long systemNo) {
        systemNotificationRepository.deleteById(systemNo);
    }

    // 특정 알림을 읽음 상태로 변경함
    @Override
    @Transactional
    public void markAsReadSystem(Long systemNo) {
        Optional<SystemNotification> notificationOpt = systemNotificationRepository.findById(systemNo);
        if (notificationOpt.isPresent()) {
            SystemNotification systemNotification = notificationOpt.get();
            systemNotification.setRead(true);
            systemNotificationRepository.save(systemNotification);
        }
    }
    // 특정 알림을 ID로 조회함
    @Override
    public SystemNotification getSystemNotificationById(Long systemNo) {
        return systemNotificationRepository.findById(systemNo)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}