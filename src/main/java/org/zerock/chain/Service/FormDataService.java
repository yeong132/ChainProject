package org.zerock.chain.service;

import org.zerock.chain.dto.FormDataDTO;

import java.util.List;

public interface FormDataService {
    List<FormDataDTO> getFormDataByDocNo(int docNo);
}
