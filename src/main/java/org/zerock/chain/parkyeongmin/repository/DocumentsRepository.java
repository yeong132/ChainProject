package org.zerock.chain.parkyeongmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.parkyeongmin.model.Documents;

import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<Documents, Integer> {

    @Query("SELECT d FROM Documents d WHERE d.loggedInEmpNo = :loggedInEmpNo ORDER BY d.docNo DESC")
    List<Documents> findSentDocuments(@Param("loggedInEmpNo") Long loggedInEmpNo);

    @Query("SELECT d FROM Documents d WHERE d.loggedInEmpNo = :loggedInEmpNo")
    List<Documents> findReceivedDocuments(@Param("loggedInEmpNo") Long loggedInEmpNo);

    @Query("SELECT d FROM Documents d WHERE d.docStatus = '임시저장' AND d.loggedInEmpNo = :loggedInEmpNo ORDER BY d.docNo DESC")
    List<Documents> findDraftDocuments(@Param("loggedInEmpNo") Long loggedInEmpNo);

    @Query("SELECT d.category FROM Documents d WHERE d.docNo = :docNo")
    String findCategoryByDocNo(@Param("docNo") int docNo);
}
