package org.zerock.chain.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.chain.parkyeongmin.model.Documents;
import org.zerock.chain.parkyeongmin.repository.DocumentsRepository;
import org.zerock.chain.parkyeongmin.service.DocumentsService;

import java.time.LocalDate;

@SpringBootTest
@Log4j2
public class DocumentsServiceTests {
    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Test
    public void testSaveDocument() {
        Documents document = Documents.builder()
                .docTitle("Test Document")
                .docStatus("Requested")
                .reqDate(LocalDate.now())
                .category("일반기안서")
                .build();

        // 임시로 준비한 formHtml 예제
        String formHtml = "<div id=\"formContainer\"><table><tr><td style=\"text-align: left; background-color: #FFFFFF;\">연장근무신청서</td></tr></table></div>";

        /*// 문서 저장
        int docNo = documentsService.saveDocument(document, formHtml);

        // 저장된 문서가 있는지 확인
        Documents savedDocument = documentsRepository.findById(docNo).orElse(null);

        // 저장된 Form 엔티티가 올바른지 확인
        Form savedForm = savedDocument.getForm();*/
    }
}