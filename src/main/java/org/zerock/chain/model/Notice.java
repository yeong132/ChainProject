package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_no")
    private Long noticeNo;

    @Column(name = "notice_name")
    private String noticeName;

    @Column(name = "notice_content")
    private String noticeContent;

    @Column(name = "notice_pinned")
    private Boolean noticePinned = false;

    @Column(name = "notice_created_date")
    private LocalDateTime noticeCreatedDate = LocalDateTime.now();

    @Column(name = "notice_files")
    private String noticeFiles;

    @Column(name = "notice_author")
    private String noticeAuthor;

    @Column(name = "notice_pinned_date")
    private LocalDate noticePinnedDate;
}