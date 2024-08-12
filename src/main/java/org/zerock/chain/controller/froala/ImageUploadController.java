package org.zerock.chain.controller.froala;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/froala")
public class ImageUploadController {

    // 업로드된 파일이 저장될 디렉토리 경로를 정의
    private static final String UPLOADS_FOLDER = "src/main/resources/static/uploads/";

    // 이미지 업로드를 처리하는 엔드포인트
    @PostMapping("/upload_image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile uploadfile) {
        return handleFileUpload(uploadfile, UPLOADS_FOLDER);
    }

    // 일반 파일 업로드를 처리하는 엔드포인트
    @PostMapping("/upload_file")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile uploadfile) {
        return handleFileUpload(uploadfile, UPLOADS_FOLDER);
    }

    // 파일 업로드를 처리하는 공통 메서드
    private ResponseEntity<Map<String, String>> handleFileUpload(MultipartFile uploadfile, String folder) {
        // 파일이 비어 있는지 확인
        if (uploadfile.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Please select a file!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // 업로드된 파일의 원본 이름에서 확장자를 추출
            String originalFilename = uploadfile.getOriginalFilename();
            String extension = originalFilename != null ? getFileExtension(originalFilename) : "";

            // UUID를 사용하여 고유한 파일 이름 생성
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            String fullPath = folder + uniqueFilename;

            // 디렉토리가 존재하지 않으면 생성
            Path destinationPath = Paths.get(fullPath);
            if (!Files.exists(destinationPath.getParent())) {
                Files.createDirectories(destinationPath.getParent());
            }

            // 파일 저장
            Files.write(destinationPath, uploadfile.getBytes());

            // 성공적으로 파일이 저장되면 파일의 URL을 반환
            Map<String, String> response = new HashMap<>();
            response.put("link", "/uploads/" + uniqueFilename);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 처리
            Map<String, String> response = new HashMap<>();
            response.put("error", "File upload failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 파일 이름에서 확장자를 추출하는 메서드
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex);
    }
}