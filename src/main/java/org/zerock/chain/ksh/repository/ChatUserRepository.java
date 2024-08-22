package org.zerock.chain.ksh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.imjongha.model.Employee;

public interface ChatUserRepository extends JpaRepository<Employee, Long> {
//    List<Employee> findActiveChatUsersByEmpNo(Long empNo);
}
