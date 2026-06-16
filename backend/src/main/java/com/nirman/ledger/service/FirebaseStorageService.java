package com.nirman.ledger.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.nirman.ledger.config.FirebaseConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseStorageService {

    private final FirebaseConfig firebaseConfig;

    @Value("${app.storage.local-dir:./uploads}")
    private String localDir;

    @Value("${app.storage.firebase-bucket:}")
    private String bucketName;

    /**
     * Upload a MultipartFile (e.g. receipt image).
     */
    public String uploadMultipartFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        try {
            return saveFile(file.getBytes(), fileName, file.getContentType());
        } catch (IOException e) {
            log.error("Failed to read file bytes: {}", e.getMessage());
            throw new RuntimeException("Could not store file", e);
        }
    }

    /**
     * Upload a generated PDF report.
     */
    public String uploadPdfReport(byte[] pdfBytes, String reportName) {
        String fileName = reportName + "_" + UUID.randomUUID().toString() + ".pdf";
        return saveFile(pdfBytes, fileName, "application/pdf");
    }

    private String saveFile(byte[] bytes, String fileName, String contentType) {
        if (firebaseConfig.isInitialized()) {
            try {
                Bucket bucket = StorageClient.getInstance().bucket();
                bucket.create(fileName, bytes, contentType);
                // Return Firebase public media URL
                return String.format(
                        "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                        bucketName,
                        URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                );
            } catch (Exception e) {
                log.error("Failed to upload to Firebase: {}. Falling back to LOCAL.", e.getMessage());
            }
        }

        // Fallback to local storage
        try {
            Path uploadPath = Paths.get(localDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path targetLocation = uploadPath.resolve(fileName);
            Files.write(targetLocation, bytes);
            log.info("File saved locally at: {}", targetLocation);
            // Returns a relative URL path that backend can serve
            return "/uploads/" + fileName;
        } catch (IOException e) {
            log.error("Failed to save file locally: {}", e.getMessage());
            throw new RuntimeException("Could not store file locally", e);
        }
    }
}
