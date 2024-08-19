package org.zerock.chain.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.model.SystemNotification;
import org.zerock.chain.repository.SystemNotificationRepository;

import java.util.List;

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
}