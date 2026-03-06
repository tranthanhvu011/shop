package com.accountshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class MinioStorageService {
    private final S3Client s3;

    @Value("${minio.bucket}") String bucket;
    @Value("${minio.publicBaseUrl}") String publicBaseUrl;

    public MinioStorageService(S3Client s3) { this.s3 = s3; }

    public String upload(MultipartFile file) throws IOException {
        String ext = "";
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) ext = name.substring(name.lastIndexOf('.')).toLowerCase();

        String key = "products/" + UUID.randomUUID() + ext;

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return publicBaseUrl + "/" + key;
    }
}