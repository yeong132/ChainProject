// FileUploadController.java
package org.zerock.chain.jy.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@RestController
public class FileUploadController {

    private static final String UPLOAD_DIR = "C:/upload/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());
            log.info("File uploaded successfully: {}", path.toString()); // 업로드된 파일 경로를 로그에 기록
            return ResponseEntity.ok("/upload/" + fileName);  // 이미지가 저장된 경로를 클라이언트에 반환
        } catch (Exception e) {
            log.error("File upload failed", e); // 오류 로그 기록
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }


}
