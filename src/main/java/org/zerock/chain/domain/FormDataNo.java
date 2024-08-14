package org.zerock.chain.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

// 복합키를 설정하는 클래스입니다!
@Data
@Embeddable
public class FormDataNo implements Serializable {
    private Integer docNo;
    private Integer fieldNo;

    public FormDataNo() {
    }

    public FormDataNo(Integer docNo, Integer fieldNo) {
        this.docNo = docNo;
        this.fieldNo = fieldNo;
    }
}

