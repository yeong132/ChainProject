package org.zerock.chain.pse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.pse.model.Chart;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
}
