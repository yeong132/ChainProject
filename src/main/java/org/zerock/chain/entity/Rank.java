package org.zerock.chain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "emp_rank")
public class Rank {

    @Id
    private int rank_no;

    private String rank_name;


}
