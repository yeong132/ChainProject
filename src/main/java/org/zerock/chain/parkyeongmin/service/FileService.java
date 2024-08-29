package org.zerock.chain.parkyeongmin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("yeongminFileService")
public class FileService {

    // 파일 크기 제한: 예를 들어 5MB로 설정
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;  // 설정 파일의 uploadDir을 사용

    public String saveFile(MultipartFile file) throws IOException {
        // 파일 크기 확인
        long fileSize = file.getSize();
        System.out.println("Uploaded file size: " + fileSize + " bytes");

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalStateException("파일 크기가 허용된 최대 크기를 초과하였습니다.");
        }

        // 파일 저장 경로 설정
        String directoryPath = uploadDir;
        Path directory = Paths.get(directoryPath);

        // 경로가 없으면 생성
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        // 파일명과 저장 경로 설정
        String fileName = file.getOriginalFilename();
        Path filePath = directory.resolve(fileName);

        // 기존 파일 삭제 후 저장
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.copy(file.getInputStream(), filePath);


        // 저장된 파일의 경로를 반환
        return filePath.toString();
    }
}
