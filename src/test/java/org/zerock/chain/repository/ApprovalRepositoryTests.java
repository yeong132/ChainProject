package org.zerock.chain.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.chain.domain.*;


import java.time.LocalDate;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Log4j2
public class ApprovalRepositoryTests {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private FormDataRepository formDataRepository;

    @Autowired
    private FormFieldsRepository formFieldsRepository;

    @Test
    public void setupDraftDocuments() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            DocumentsEntity document = DocumentsEntity.builder()
                    .docTitle("결재 문서입니다. 확인 부탁드립니다.")
                    .docStatus("임시저장")
                    .draftDate(LocalDate.now())
                    .senderEmpNo(1) // 사원 번호
                    .category("일반 기안")
                    .build();
            documentsRepository.save(document);
        });
    }

    @Test
    public void setupRequestDocuments() {
        IntStream.rangeClosed(1, 5).forEach(i -> {
            DocumentsEntity document = DocumentsEntity.builder()
                    .docTitle("결재 문서입니다. 확인 부탁드립니다.")
                    .docStatus("요청")
                    .reqDate(LocalDate.now())
                    .senderEmpNo(1) // 보내는 사원 번호
                    .receiverEmpNo(2) // 받는 사원 번호
                    .category("일반기안")
                    .formNo(1)
                    .build();
            documentsRepository.save(document);
        });
    }

    @Test
    public void setupEmployees() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            EmployeesEntity employee = EmployeesEntity.builder()
                    .firstName("First" + i)
                    .lastName("Last" + i)
                    .hireDate(LocalDate.now().minusYears(1))
                    .lastDate(null)
                    .email("email" + i + "@test.com")
                    .phoneNum("010-1234-567" + i)
                    .profileImg("profile" + i + ".png")
                    .build();
            employeesRepository.save(employee);
        });
    }

    @Test
    public void setupApproval() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            DocumentsEntity document = documentsRepository.findById(i).orElseThrow();
            EmployeesEntity employee = employeesRepository.findById(i).orElseThrow();

            ApprovalEntity approvalEntity = ApprovalEntity.builder()
                    .docNo(document)
                    .empNo(employee)
                    .approvalDate(LocalDate.now())
                    .rejectionReason("Sample rejection reason " + i)
                    .build();

            approvalRepository.save(approvalEntity);
        });
    }

    @Test
    public void setupFormData() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            DocumentsEntity document = documentsRepository.findById(i).orElseThrow();
            FormFieldsEntity formField = formFieldsRepository.findById(i).orElseThrow(); // formFieldsRepository 필요

            FormDataNo formDataNo = new FormDataNo(document.getDocNo(), formField.getFieldNo());

            FormDataEntity formData = FormDataEntity.builder()
                    .formDataNo(formDataNo)  // 복합키 설정
                    .fieldValue("Sample value " + i)
                    .build();
            formDataRepository.save(formData);
        });
    }

    @Test
    public void setupFormFields() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            FormFieldsEntity formField = FormFieldsEntity.builder()
                    .formNo(1) // form_no를 Integer로 설정
                    .fieldNo(i)
                    .fieldName("입력란 " + i)
                    .fieldType("text") // 예시로 'text' 사용
                    .fieldOptions("Option " + i)
                    .category("일반기안")
                    .build();
            formFieldsRepository.save(formField);
        });
    }

    @Test
    public void testSave() {
        DocumentsEntity document = DocumentsEntity.builder()
                .docTitle("결재 올려드립니다")
                .docStatus("요청")
                .reqDate(LocalDate.now())
                .senderEmpNo(1)
                .receiverEmpNo(2)
                .category("일반기안")
                .formNo(1)
                .build();

        DocumentsEntity savedDocument = documentsRepository.save(document);
        assertNotNull(savedDocument);
        assertEquals(document.getDocTitle(), savedDocument.getDocTitle());
    }
}