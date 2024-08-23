package org.zerock.chain.parkyeongmin.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "DepartmentParkyeongmin")
@Table(name = "department")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dmp_no")
    private Long dmpNo;

    @Column(name = "dmp_name", nullable = false, length = 100)
    private String dmpName;
}
