package org.zerock.chain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {

    // Getters and Setters
    @Id
    private int dmp_no; // 부서 번호

    private String dmp_name; // 부서명

}