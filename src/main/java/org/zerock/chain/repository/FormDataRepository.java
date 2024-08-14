package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.domain.FormDataEntity;
import org.zerock.chain.domain.FormDataNo;

import java.util.List;

@Repository
public interface FormDataRepository extends JpaRepository<FormDataEntity, FormDataNo> {

    // JPQL 쿼리로 복합키의 docNo 필드에 기반한 검색
    @Query("SELECT f FROM FormDataEntity f WHERE f.formDataNo.docNo = :docNo")
    List<FormDataEntity> findByDocNo(@Param("docNo") int docNo);
}
