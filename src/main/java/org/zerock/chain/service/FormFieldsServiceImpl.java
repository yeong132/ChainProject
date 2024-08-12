package org.zerock.chain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.domain.FormFieldsEntity;
import org.zerock.chain.dto.FormFieldsDTO;
import org.zerock.chain.repository.FormFieldsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormFieldsServiceImpl implements FormFieldsService {

    @Autowired
    private FormFieldsRepository formFieldsRepository;

    @Override
    public List<FormFieldsDTO> getFormFieldsByCategory(String category) {
        return formFieldsRepository.findByCategory(category).stream()
                .map(field -> new FormFieldsDTO(
                        field.getFieldNo(),
                        field.getFormNo(),
                        field.getFieldName(),
                        field.getFieldType(),
                        field.getFieldOptions(),
                        field.getCategory()
                ))
                .collect(Collectors.toList());
    }
}
