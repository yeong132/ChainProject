package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "system_notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
// 시스템 관리자
public class SystemNotification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_no")
    private Long systemNo;

    @Column(name = "system_category")
    private String systemCategory;

    @Column(name = "system_title")
    private String systemTitle;

    @Column(name = "system_content")
    private String systemContent;

    @Column(name = "system_upload_date")
    private LocalDate systemUploadDate;


}
