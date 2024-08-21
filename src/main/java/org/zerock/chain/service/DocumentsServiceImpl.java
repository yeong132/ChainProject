package org.zerock.chain.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.domain.DocumentsEntity;
import org.zerock.chain.domain.FormDataEntity;
import org.zerock.chain.domain.FormDataNo;
import org.zerock.chain.domain.FormFieldsEntity;
import org.zerock.chain.dto.DocumentsDTO;
import org.zerock.chain.dto.FormDataDTO;
import org.zerock.chain.dto.FormFieldsDTO;
import org.zerock.chain.repository.DocumentsRepository;
import org.zerock.chain.repository.FormDataRepository;
import org.zerock.chain.repository.FormFieldsRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class DocumentsServiceImpl implements DocumentsService<DocumentsDTO> {

    @Autowired
    private DocumentsRepository documentsRepository;
    @Autowired
    private FormFieldsRepository formFieldsRepository;
    @Autowired
    private FormDataRepository formDataRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public DocumentsDTO getDocumentById(int docNo) {
        DocumentsEntity document = documentsRepository.findById(docNo).orElseThrow(() -> new RuntimeException("Document not found"));
        return modelMapper.map(document, DocumentsDTO.class);
    }

    @Override
    public List<DocumentsDTO> getAllDocuments() {
        List<DocumentsEntity> documents = documentsRepository.findAll();
        return documents.stream()
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .toList();
    }

    @Override
    public int registerDocument(DocumentsDTO documentsDTO) {
        DocumentsEntity document = modelMapper.map(documentsDTO, DocumentsEntity.class);
        DocumentsEntity savedDocument = documentsRepository.save(document);

        return savedDocument.getDocNo();
    }

    /*@PostConstruct // 서비스 테스트 때 3개의 Date중 하나만 인식해서 생긴 오류 때문에 넣은 코드
    public void configureModelMapper() {
        TypeMap<DocumentsDTO, DocumentsEntity> typeMap = modelMapper.createTypeMap(DocumentsDTO.class, DocumentsEntity.class);
        typeMap.addMappings(mapper -> {
            mapper.map(DocumentsDTO::getReqDate, DocumentsEntity::setReqDate);
            mapper.map(DocumentsDTO::getReReqDate, DocumentsEntity::setReReqDate);
            mapper.map(DocumentsDTO::getDraftDate, DocumentsEntity::setDraftDate);
        });
    }*/

    @Override
    public List<DocumentsDTO> getSentDocuments(Integer senderEmpNo) {
        // 보낸 문서 목록을 조회하여 DTO로 변환
        List<DocumentsEntity> documents = documentsRepository.findSentDocuments(senderEmpNo);
        return documents.stream()
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentsDTO> getReceivedDocuments(Integer receiverEmpNo) {
        // 받은 문서 목록을 조회하여 DTO로 변환
        List<DocumentsEntity> documents = documentsRepository.findReceivedDocuments(receiverEmpNo);
        return documents.stream()
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentsDTO> getDraftDocuments() {
        // 임시 문서 목록을 조회하여 DTO로 변환
        List<DocumentsEntity> documents = documentsRepository.findDraftDocuments();
        return documents.stream()
                .map(doc -> modelMapper.map(doc, DocumentsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String getCategoryByDocNo(int docNo) {
        return documentsRepository.findCategoryByDocNo(docNo);
    }

    @Override
    public int saveDocument(DocumentsEntity documentsEntity, List<FormFieldsDTO> formFields, Map<Integer, String> formData) {
        // 문서 저장
        DocumentsEntity savedDocument = documentsRepository.save(documentsEntity);
        int docNo = savedDocument.getDocNo();


        // form_fields 저장
        if (formFields != null) {
            saveFormFields(formFields);
        }

        // form_data 저장
        if (formData != null) {
            saveFormData(docNo, formData);
        }

        return docNo;
    }

    // form_fields 데이터를 저장하는 메서드
    private void saveFormFields(List<FormFieldsDTO> formFields) {
        for (FormFieldsDTO fieldDTO : formFields) {
            FormFieldsEntity formField = new FormFieldsEntity();
            formField.setFormNo(fieldDTO.getFormNo());
            formField.setFieldNo(fieldDTO.getFieldNo());
            formField.setFieldType(fieldDTO.getFieldType());
            formField.setFieldOptions(fieldDTO.getFieldOptions());
            formField.setFieldName(fieldDTO.getFieldName());  // 필드 이름 추가

            formFieldsRepository.save(formField);
        }
    }

    // form_data 데이터를 저장하는 메서드
    private void saveFormData(int docNo, Map<Integer, String> formData) {
        for (Map.Entry<Integer, String> entry : formData.entrySet()) {
            Integer fieldNo = entry.getKey();
            String fieldValue = entry.getValue();

            FormDataEntity formDataEntity = new FormDataEntity();
            FormDataNo formDataNo = new FormDataNo(docNo, fieldNo); // 복합키 객체 생성
            formDataEntity.setFormDataNo(formDataNo); // 복합키 설정
            formDataEntity.setFieldValue(fieldValue);

            formDataRepository.save(formDataEntity);
        }
    }
}
