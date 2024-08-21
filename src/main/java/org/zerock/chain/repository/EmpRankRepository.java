package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.model.Rank;

@Repository
public interface EmpRankRepository extends JpaRepository<Rank, Long> {
}