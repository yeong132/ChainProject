package org.zerock.chain.pse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.pse.model.Qna;

public interface QnaRepository extends JpaRepository<Qna, Long> {
}