package org.zerock.chain.pse.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.parkyeongmin.model.Approval;
import org.zerock.chain.parkyeongmin.model.Documents;
import org.zerock.chain.parkyeongmin.repository.ApprovalRepository;
import org.zerock.chain.parkyeongmin.repository.DocumentsRepository;
import org.zerock.chain.parkyeongmin.service.ApprovalService;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2 // 로그 확인을 위해 임시로 추가 << 확인하고나서 삭제할것
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired  // (영민)이 추가
    private ApprovalRepository approvalRepository;
    @Autowired  // (영민)이 추가
    private DocumentsRepository documentsRepository;

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
    public void createApprovalNotification(int docNo, String docTitle, String senderName, String docStatus, String withdraw) {
        // 결재자 사원의 번호를 가져옴
        List<Long> empNos = approvalRepository.findEmpNosByDocNo(docNo);
        // 참조자 사원의 번호를 가져옴
        List<Long> refEmpNos = approvalRepository.findRefEmpNosByDocNo(docNo);

        // 기안자(작성자)의 사원 번호를 가져옴
        Documents document = documentsRepository.findById(docNo)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        Long senderEmpNo = document.getLoggedInEmpNo();  // logged_in_emp_no에서 기안자의 사원 번호 가져오기

        // 모든 관련 사원의 번호를 하나의 리스트로 병합
        Set<Long> allEmpNos = new HashSet<>();
        allEmpNos.addAll(empNos);
        allEmpNos.addAll(refEmpNos);
        allEmpNos.add(senderEmpNo);  // 기안자도 추가

        // 각 사원에게 알림 생성
        for (Long empNoLong : allEmpNos) {
            if (empNoLong != null) {
                int empNo = empNoLong.intValue();  // Long을 int로 변환

                String message;

                if ("true".equals(withdraw)) {
                    // 철회된 문서에 대한 맞춤형 알림 메시지
                    message = String.format("%s 님이 요청한 %d번 문서 '%s'이(가) 철회되었습니다.", senderName, docNo, docTitle);
                } else if (isCurrentApprover(docNo, empNoLong)) {
                    // 현재 결재 차례인 사람에게 맞춤형 알림 메시지
                    message = String.format("%s 님이 요청한 %d번 문서 '%s'의 결재를 진행해주십시오.", senderName, docNo, docTitle);
                } else {
                    // 일반적인 결재 완료/반려 메시지
                    message = String.format("%s 님이 요청한 %d번 문서 '%s'의 현재 결재 상태는 %s입니다.", senderName, docNo, docTitle, docStatus);
                }

                Notification notification = new Notification();
                notification.setEmpNo(empNo);  // int로 변환된 값을 사용
                notification.setIsRead(false);
                notification.setNotificationDate(notification.getNotificationDate());
                notification.setNotificationMessage(message);
                notification.setNotificationType("전자결재");
                notification.setReferenceId(0L);

                log.info("NotificationDate: {}", notification.getNotificationDate());
                notificationRepository.save(notification);
                log.info("Saved Notification with Date: {}", notification.getNotificationDate());
            }
        }
    }

    // (영민)이 추가
    private boolean isCurrentApprover(int docNo, Long empNo) {
        // 현재 사용자의 Approval 정보 조회
        Approval currentApproval = approvalRepository.findByDocumentsDocNoAndEmployeeEmpNo(docNo, empNo);

        if (currentApproval != null) {
            int currentOrder = currentApproval.getApprovalOrder();

            // 이전 결재자의 승인 여부 확인
            if (currentOrder > 1) {
                Approval previousApproval = approvalRepository.findByDocumentsDocNoAndApprovalOrder(docNo, currentOrder - 1);

                // 이전 결재자가 승인했을 때만 현재 결재자가 승인/반려 버튼을 볼 수 있음
                return previousApproval != null && "승인".equals(previousApproval.getApprovalStatus()) &&
                        "대기".equals(currentApproval.getApprovalStatus());
            } else {
                // 첫 번째 결재자라면 이전 결재자가 없으므로 바로 승인/반려 가능
                return "대기".equals(currentApproval.getApprovalStatus());
            }
        }
        return false;
    }
}
