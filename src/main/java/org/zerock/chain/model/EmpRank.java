package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "emp_rank")
public class EmpRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankNo;

    @Column(name = "rank_name")
    private String rankName;
}
