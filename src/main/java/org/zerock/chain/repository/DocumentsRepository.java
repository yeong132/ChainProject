package org.zerock.chain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.domain.DocumentsEntity;

import java.util.List;

@Repository
public interface DocumentsRepository extends JpaRepository<DocumentsEntity, Integer> {

    @Query("SELECT d FROM DocumentsEntity d WHERE d.senderEmpNo = :senderEmpNo")
    List<DocumentsEntity> findSentDocuments(@Param("senderEmpNo") Integer senderEmpNo);

    @Query("SELECT d FROM DocumentsEntity d WHERE d.receiverEmpNo = :receiverEmpNo")
    List<DocumentsEntity> findReceivedDocuments(@Param("receiverEmpNo") Integer receiverEmpNo);

    @Query("SELECT d FROM DocumentsEntity d WHERE d.docStatus = '임시저장'")
    List<DocumentsEntity> findDraftDocuments();

    @Query("SELECT d.formNo FROM DocumentsEntity d WHERE d.docNo = :docNo")
    Integer findFormNoByDocNo(@Param("docNo") Integer docNo);

    @Query("SELECT d.category FROM DocumentsEntity d WHERE d.docNo = :docNo")
    String findCategoryByDocNo(@Param("docNo") int docNo);
}
