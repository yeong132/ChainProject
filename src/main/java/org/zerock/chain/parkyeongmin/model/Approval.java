package org.zerock.chain.parkyeongmin.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "approvals")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_no")
    private Long approvalNo;  // 결재번호

    @ManyToOne
    @JoinColumn(name = "doc_no", referencedColumnName = "doc_no")
    private Documents documents;    // 문서 번호

    @ManyToOne
    @JoinColumn(name = "emp_no", referencedColumnName = "emp_no")
    private Employee employee;      // 결재자 사원 번호

    @Column(name = "approval_date", nullable = true)
    private LocalDate approvalDate;  // 결재일 (최종 결재일)

    @Column(name = "rejection_reason")
    private String rejectionReason;  // 결재 반려 사유

    @Column(name = "approval_order")
    private Integer approvalOrder;  // 결재 순서
}
