package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.model.User;
import org.zerock.chain.model.Status;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<User, String> {
    List<User> findAllByStatus(Status status);
}
