package org.zerock.chain.imjongha.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "department")
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dmp_no")
    private Long dmpNo;

    @Column(name = "dmp_name", nullable = false)
    private String dmpName;

}
