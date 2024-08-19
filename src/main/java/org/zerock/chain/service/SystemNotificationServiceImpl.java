package org.zerock.chain.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Override
    public void saveSystemNotification(SystemNotification systemNotification) {
        systemNotificationRepository.save(systemNotification);
    }

    @Override
    public List<SystemNotification> getAllSystemNotifications() {
        return systemNotificationRepository.findAll();
    }
}