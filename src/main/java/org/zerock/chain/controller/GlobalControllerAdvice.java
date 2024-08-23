package org.zerock.chain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.zerock.chain.model.Notification;
import org.zerock.chain.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("allNotifications")
    public List<Notification> populateNotifications(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User userDetails = (User) authentication.getPrincipal();
            String username = userDetails.getUsername();
            int empNo = getEmpNoByUsername(username);
            return notificationService.getAllNotifications(empNo);
        }
        return new ArrayList<>();
    }

    private int getEmpNoByUsername(String username) {
        // 사원번호를 조회하는 실제 로직 구현
        return 1; // 실제 DB 조회 로직으로 변경해야 합니다.
    }
}
