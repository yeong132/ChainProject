package org.zerock.chain.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDataEntity {

    @EmbeddedId
    private FormDataNo formDataNo;

    @Column(name = "field_value")
    private String fieldValue;

}
