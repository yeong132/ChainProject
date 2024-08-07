package org.zerock.chain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "emp_rank_mapping")
public class EmpRankMapping {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "emp_no")
    private int emp_no;

    @Column(name = "rank_no")
    private int rank_no;

}
