package com.accountshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * FileStorageService — local file storage for uploads (products, avatars, chat files).
 */
@Slf4j
@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:./uploads}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.uploadDir.resolve("avatars"));
            Files.createDirectories(this.uploadDir.resolve("products"));
            Files.createDirectories(this.uploadDir.resolve("chat"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    /**
     * Store generic file.
     */
    public String storeFile(MultipartFile file, String subfolder) {
        String filename = generateFilename(file.getOriginalFilename());
        try {
            Path targetDir = uploadDir.resolve(subfolder);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored: {}/{}", subfolder, filename);
            return "/uploads/" + subfolder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    /**
     * Store avatar image.
     */
    public String storeAvatar(MultipartFile file) {
        return storeFile(file, "avatars");
    }

    /**
     * Store product image.
     */
    public String storeProductImage(MultipartFile file) {
        return storeFile(file, "products");
    }

    /**
     * Store chat file.
     */
    public String storeChatFile(MultipartFile file, Long conversationId) {
        return storeFile(file, "chat/" + conversationId);
    }

    /**
     * Delete a file.
     */
    public boolean deleteFile(String fileUrl) {
        try {
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = uploadDir.resolve(relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", fileUrl, e);
            return false;
        }
    }

    private String generateFilename(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + ext;
    }
}
