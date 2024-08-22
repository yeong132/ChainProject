package org.zerock.chain.ksh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.ksh.model.User;
import org.zerock.chain.ksh.model.Status;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<User, String> {
    List<User> findAllByStatus(Status status);
}
