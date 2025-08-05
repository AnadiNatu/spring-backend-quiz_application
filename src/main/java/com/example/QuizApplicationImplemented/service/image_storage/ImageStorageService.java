package com.example.QuizApplicationImplemented.service.image_storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {
    @Value("${file.product-upload-dir}")
    private String uploadDir;

    public String save(MultipartFile file) {
        try {
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "_" + originalName;

            Path path = Paths.get(uploadDir).resolve(filename);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream() , path , StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/products/" + filename;
        }catch (IOException ex){
            throw new RuntimeException("Could not store images " + ex.getMessage());
        }
    }
}
