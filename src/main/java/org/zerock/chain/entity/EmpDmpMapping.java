package org.zerock.chain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "emp_dmp_mapping")
public class EmpDmpMapping {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "emp_no")
    private int emp_no;

    @Column(name = "dmp_no")
    private int dmp_no;

}