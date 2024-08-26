package org.zerock.chain.pse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.pse.model.Chart;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {

    List<Chart> findByChartAuthor(Long chartAuthor);
    Optional<Chart> findByChartNoAndChartAuthor(Long chartNo, Long chartAuthor);
}
