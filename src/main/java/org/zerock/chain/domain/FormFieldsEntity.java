package org.zerock.chain.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_fields")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_no")
    private Integer fieldNo;

    @Column(name = "form_no")
    private Integer formNo;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_type")
    private String fieldType;

    @Column(name = "field_options")
    private String fieldOptions;

    @Column(name = "category")
    private String category;

}
