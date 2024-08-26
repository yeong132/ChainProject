package org.zerock.chain.parkyeongmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.zerock.chain.parkyeongmin.model.Approval;
import org.zerock.chain.parkyeongmin.model.Documents;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    // 특정 결재자의 모든 문서를 docNo로 최신순으로 조회
    List<Approval> findByEmployeeEmpNoOrderByDocumentsDocNoDesc(Long empNo);

    // 결재자가 어떤 순서에 있든 자신에게 지정된 모든 결재 문서를 조회
    Approval findByDocumentsDocNoAndEmployeeEmpNo(int docNo, Long empNo);

    // 특정 결재자(empNo)가 현재 결재할 차례인 문서 조회
    List<Approval> findByEmployeeEmpNoAndApprovalStatus(Long empNo, String approvalStatus);

    // 특정 문서 번호와 결재 순서로 결재 내역을 조회
    Approval findByDocumentsDocNoAndApprovalOrder(int docNo, int approvalOrder);

    // 특정 문서 번호의 모든 결재 내역을 조회 (결재선 조회 시 사용 가능)
    List<Approval> findByDocumentsDocNo(int docNo);

    // 로그인한 결재자별로 대기 상태인 문서 개수 조회
    @Query("SELECT COUNT(a) FROM Approval a WHERE a.approvalStatus = '대기' AND a.employee.empNo = :empNo")
    int countPendingApprovalsByEmpNo(@Param("empNo") Long empNo);

    // 로그인한 결재자별로 승인 상태인 문서 개수 조회
    @Query("SELECT COUNT(a) FROM Approval a WHERE a.approvalStatus = '승인' AND a.employee.empNo = :empNo")
    int countApprovedApprovalsByEmpNo(@Param("empNo") Long empNo);

    // 로그인한 결재자의 반려된 문서가 하나라도 있다면 개수로 추가
    @Query("SELECT COUNT(DISTINCT a1.documents.docNo) " +
            "FROM Approval a1 " +
            "WHERE a1.employee.empNo = :empNo " +
            "AND EXISTS (" +
            "    SELECT 1 FROM Approval a2 " +
            "    WHERE a2.documents.docNo = a1.documents.docNo AND a2.approvalStatus = '반려'" +
            ")")
    int countRejectedDocumentsForApprover(@Param("empNo") Long empNo);
}
