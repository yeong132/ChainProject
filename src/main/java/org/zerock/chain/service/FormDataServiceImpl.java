package org.zerock.chain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.chain.dto.FormDataDTO;
import org.zerock.chain.repository.FormDataRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormDataServiceImpl implements FormDataService {
    @Autowired
    private FormDataRepository formDataRepository;

    @Override
    public List<FormDataDTO> getFormDataByDocNo(int docNo) {
        return formDataRepository.findByDocNo(docNo).stream()
                .map(data -> new FormDataDTO(data.getFormDataNo().getDocNo(), data.getFormDataNo().getFieldNo(), data.getFieldValue()))
                .collect(Collectors.toList());
    }
}
