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

    private static final String UPLOADED_FOLDER = "src/main/resources/static/uploads/";

    @PostMapping("/upload_image")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile uploadfile) {
        if (uploadfile.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Please select a file!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        try {
            String originalFilename = uploadfile.getOriginalFilename();
            String extension = originalFilename != null ? getFileExtension(originalFilename) : "";
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            saveUploadedFiles(uploadfile, uniqueFilename);

            Map<String, String> response = new HashMap<>();
            response.put("link", "/uploads/" + uniqueFilename);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "File upload failed!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    private void saveUploadedFiles(MultipartFile file, String filename) throws IOException {
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + filename);
            Files.write(path, bytes);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex);
    }
}