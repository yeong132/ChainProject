package org.zerock.chain.pse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.pse.model.Todo;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByEmpNo(Long empNo);
    List<Todo> findByEmpNoAndTodoFavorite(Long empNo, boolean todoFavorite);
    List<Todo> findByEmpNoAndTodoStatus(Long empNo, boolean todoStatus);
}
