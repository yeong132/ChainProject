package org.zerock.chain.parkyeongmin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerock.chain.parkyeongmin.model.Approval;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Integer> {

    // 특정 문서 번호와 결재자 사원 번호로 결재 내역을 조회
    Approval findByDocNoAndEmpNo(int docNo, Long empNo);

    // 특정 문서 번호와 결재 순서로 결재 내역을 조회
    Approval findByDocNoAndApprovalOrder(int docNo, int approvalOrder);

    // 특정 문서 번호의 모든 결재 내역을 조회 (결재선 조회 시 사용 가능)
    List<Approval> findByDocNo(int docNo);

}
