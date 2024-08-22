package org.zerock.chain.pse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.pse.model.Todo;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 즐겨찾기, 완료 상태에 따른 항목 조회 메서드 추가
    List<Todo> findByTodoFavoriteTrue();
    List<Todo> findByTodoStatusFalse();
    List<Todo> findByTodoStatusTrue();
    List<Todo> findByTodoStatus(boolean todoStatus);
}