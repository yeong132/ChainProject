package org.zerock.chain.parkyeongmin.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileDownloadController {

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) throws IOException {
        // 파일 경로를 URL 디코딩
        String decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

        // 파일 리소스를 가져옴
        Path path = Paths.get(decodedFilePath);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            // 파일이 존재하고 읽을 수 있는 경우 다운로드를 위한 응답 생성
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }
}

