package com.group13.reportsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    public String store(MultipartFile file) throws IOException {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename());
        String savedName = UUID.randomUUID() + "-" + originalName.replace(" ", "_");
        Path target = uploadPath.resolve(savedName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return savedName;
    }

    public Resource loadAsResource(String storedPath) throws MalformedURLException {
        Path file = uploadPath.resolve(storedPath).normalize();
        return new UrlResource(file.toUri());
    }

    public String probeContentType(String storedPath) throws IOException {
        Path file = uploadPath.resolve(storedPath).normalize();
        String contentType = Files.probeContentType(file);
        return contentType == null ? "application/octet-stream" : contentType;
    }

    public void ensureSeedFile(String relativePath, String content) throws IOException {
        Path file = uploadPath.resolve(relativePath).normalize();
        Files.createDirectories(file.getParent());
        if (!Files.exists(file)) {
            Files.writeString(file, content);
        }
    }
}
