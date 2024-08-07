package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.chain.entity.EmpRankMapping;

public interface EmpRankMappingRepository extends JpaRepository<EmpRankMapping, Integer> {
}
