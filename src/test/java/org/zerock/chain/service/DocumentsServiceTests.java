package org.zerock.chain.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.chain.domain.DocumentsEntity;
import org.zerock.chain.domain.FormDataEntity;
import org.zerock.chain.domain.FormFieldsEntity;
import org.zerock.chain.dto.DocumentsDTO;
import org.zerock.chain.dto.FormFieldsDTO;
import org.zerock.chain.repository.DocumentsRepository;
import org.zerock.chain.repository.FormDataRepository;
import org.zerock.chain.repository.FormFieldsRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class DocumentsServiceTests {
  @Autowired
  private DocumentsService documentsService;

  @Autowired
  private DocumentsRepository documentsRepository;

    @Autowired
    private FormFieldsRepository formFieldsRepository;

    @Autowired
    private FormDataRepository formDataRepository;

  @Test
  public void testRegister() {
   log.info(documentsService.getClass().getName());

   DocumentsDTO documentsDTO = DocumentsDTO.builder()
           .docTitle("결재 올려드립니다")
           .reqDate(LocalDate.now())
           .docStatus("요청")
           .senderEmpNo(1)
           .build();

   int docNo = documentsService.registerDocument(documentsDTO);
   log.info("Registered Document No: " + docNo);
  }


    @Test
    public void testSaveDocument() {
        DocumentsEntity document = DocumentsEntity.builder()
                .docTitle("Test Document")
                .docStatus("Requested")
                .reqDate(LocalDate.now())
                .senderEmpNo(1)
                .receiverEmpNo(2)
                .category("일반기안")
                .formNo(1)
                .build();

        // Prepare formFields
        List<FormFieldsDTO> formFields = Arrays.asList(
                new FormFieldsDTO(1, 1,"fieldName1", "text", "maxLength=50", "일반기안"),
                new FormFieldsDTO(2, 1,"fieldName2", "date", "format=yyyy-MM-dd", "일반기안")
        );

        // Prepare formData
        Map<Integer, String> formData = new HashMap<>();
        formData.put(1, "Test Data");
        formData.put(2, "2024-08-09");

        // Call the service method
        int docNo = documentsService.saveDocument(document, formFields, formData);

        // Verify the document is saved
        DocumentsEntity savedDocument = documentsRepository.findById(docNo).orElse(null);
        assertNotNull(savedDocument);
        assertEquals("Test Document", savedDocument.getDocTitle());

        // Verify the formFields are saved
        List<FormFieldsEntity> savedFormFields = formFieldsRepository.findAll();
        assertEquals(2, savedFormFields.size());
        assertTrue(savedFormFields.stream().anyMatch(ff -> "fieldName1".equals(ff.getFieldName())));

        // Verify the formData is saved
        List<FormDataEntity> savedFormData = formDataRepository.findAll();
        assertEquals(2, savedFormData.size());
        assertTrue(savedFormData.stream().anyMatch(fd -> "Test Data".equals(fd.getFieldValue())));
    }
}