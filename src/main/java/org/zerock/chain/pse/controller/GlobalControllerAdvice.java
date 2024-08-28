package org.zerock.chain.pse.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zerock.chain.pse.model.Notification;
import org.zerock.chain.pse.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("allNotifications")
    public List<Notification> populateNotifications(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // 세션에서 사원번호(empNo) 가져오기
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            Long empNo = (Long) session.getAttribute("empNo");  // 세션에 저장된 사원번호 가져오기

            if (empNo != null) {
                // 사원번호를 통해 모든 알림을 가져오기
                return notificationService.getAllNotifications(empNo);
            }
        }
        // 인증되지 않았거나 사원번호가 없는 경우 빈 리스트 반환
        return new ArrayList<>();
    }
}
