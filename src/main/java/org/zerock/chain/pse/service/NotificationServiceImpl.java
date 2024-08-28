package org.zerock.chain.pse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.parkyeongmin.repository.ApprovalRepository;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ApprovalRepository approvalRepository;

    @Override
    public List<Notification> getNotificationsByType(int empNo, String type) {
        return notificationRepository.findByEmpNoAndNotificationType(empNo, type);
    }

    @Override
    public List<Notification> getAllNotifications(int empNo) {
        return notificationRepository.findByEmpNo(empNo);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(int empNo) {
        notificationRepository.deleteByEmpNo(empNo);
    }

    @Override
    public void deleteNotification(Long notificationNo) {
        notificationRepository.deleteById(notificationNo);
    }

    @Override  // 전자결재 알림 생성 메서드 (영민)
    public void createApprovalNotification(int docNo, String docTitle, String senderName, String docStatus) {
        // approvals 테이블에서 해당 문서에 관련된 모든 사용자(emp_no)를 가져옴
        List<Long> empNos = approvalRepository.findAllRelatedEmpNosByDocNo(docNo);

        // 각 사용자에게 알림 생성
        for (Long empNoLong : empNos) {
            int empNo = empNoLong.intValue();  // Long을 int로 변환

            String message = String.format("%s님이 요청한 %d번의 %s이(가) 결재 %s되었습니다.",
                    senderName, docNo, docTitle, docStatus);

            Notification notification = new Notification();
            notification.setEmpNo(empNo);  // int로 변환된 값을 사용
            notification.setIsRead(false);
            notification.setNotificationDate(LocalDateTime.now());
            notification.setNotificationMessage(message);
            notification.setNotificationType("전자결재");
            notification.setReferenceId(0L);

            notificationRepository.save(notification);
        }
    }

}
