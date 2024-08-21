package org.zerock.chain.repository;

import org.zerock.chain.model.Signup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignupRepository extends JpaRepository<Signup, Long> {
    Optional<Signup> findByEmpNo(Long empNo);
}