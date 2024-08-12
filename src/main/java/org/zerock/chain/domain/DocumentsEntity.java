package org.zerock.chain.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_no")
    private Integer docNo;

    @Column(name = "form_no")
    private Integer formNo;  // 양식 번호

    @Column(name = "sender_emp_no")
    private Integer senderEmpNo;

    @Column(name = "receiver_emp_no")
    private Integer receiverEmpNo;

    @Column(name = "doc_title", nullable = false)
    private String docTitle;

    @Column(name = "doc_status")
    private String docStatus;

    @Column(name = "req_date")
    private LocalDate reqDate;

    @Column(name = "re_req_date")
    private LocalDate reReqDate;

    @Column(name = "draft_date")
    private LocalDate draftDate;

    @Column(name = "category")
    private String category;

    @Override
    public String toString() {
        return "DocumentsEntity{" +
                "docNo=" + docNo +
                ", docTitle='" + docTitle + '\'' +
                ", docStatus='" + docStatus + '\'' +
                ", reqDate=" + reqDate +
                ", senderEmpNo=" + senderEmpNo +
                ", receiverEmpNo=" + receiverEmpNo +
                ", formNo='" + formNo + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
