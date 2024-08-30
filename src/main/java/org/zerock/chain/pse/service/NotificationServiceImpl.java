package org.zerock.chain.pse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.repository.NotificationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl extends BaseService<Notification> implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // 사원번호와 알림 타입으로 알림 상태 업데이트
    @Override
    @Transactional
    public void updateNotificationSettingByType(Long empNo, String notificationType, Boolean enabled) {
        notificationRepository.updateEnabledByEmpNoAndType(empNo, notificationType, enabled);
    }

    // 특정 사원번호(empNo)로 모든 알림을 가져옴
    @Override
    protected List<Notification> getAllItemsByEmpNo(Long empNo) {
        return notificationRepository.findByEmpNo(empNo);
    }

    // 특정 타입의 알림을 가져옴
    @Override
    public List<Notification> getNotificationsByType(Long empNo, String type) {
        return getItemsByEmpNo(empNo, empNoParam -> notificationRepository.findByEmpNoAndNotificationType(empNoParam, type));
    }

    // 특정 사원의 모든 알림을 최신순으로 가져옴
    @Override
    public List<Notification> getAllNotifications(Long empNo) {
        return getItemsByEmpNo(empNo, this::getAllItemsByEmpNo).stream()
                .sorted(Comparator.comparing(Notification::getNotificationDate).reversed()) // 최신순으로 정렬
                .collect(Collectors.toList());
    }

    // 특정 사원의 모든 알림을 삭제함
    @Override
    @Transactional
    public void deleteAllNotifications(Long empNo) {
        notificationRepository.deleteByEmpNo(empNo);
    }

    // 특정 알림을 삭제함
    @Override
    public void deleteNotification(Long notificationNo) {
        notificationRepository.deleteById(notificationNo);
    }

    // 특정 알림을 읽음 상태로 변경함
    @Override
    @Transactional
    public void markAsRead(Long notificationNo) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationNo);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    // 특정 알림을 ID로 조회함
    @Override
    public Notification getNotificationById(Long notificationNo) {
        return notificationRepository.findById(notificationNo)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    // 새로운 메서드: 특정 사원의 읽은 알림을 모두 삭제함
    @Override
    @Transactional
    public void deleteReadNotifications(Long empNo) {
        // NotificationRepository의 새로운 메서드를 호출하여 읽은 알림을 삭제
        notificationRepository.deleteByEmpNoAndIsRead(empNo);
    }
}
