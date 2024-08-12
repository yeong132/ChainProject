package org.zerock.chain.service;

import org.zerock.chain.domain.FormFieldsEntity;
import org.zerock.chain.dto.FormFieldsDTO;

import java.util.List;

public interface FormFieldsService {
    List<FormFieldsDTO> getFormFieldsByCategory(String category);
}
