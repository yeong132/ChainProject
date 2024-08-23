package org.zerock.chain.ksh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.ksh.model.ChatNotification;

@Repository
public interface ChatNotificationRepository extends JpaRepository<ChatNotification, Long> {
}
