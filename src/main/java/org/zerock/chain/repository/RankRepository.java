package org.zerock.chain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.zerock.chain.entity.Rank;

public interface RankRepository extends JpaRepository<Rank, Integer> {
    // 기본 CRUD 작업을 위한 메서드는 CrudRepository에서 제공됨
}