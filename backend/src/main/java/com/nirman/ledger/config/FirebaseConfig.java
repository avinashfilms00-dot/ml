package com.nirman.ledger.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${app.storage.firebase-config-path}")
    private String configPath;

    @Value("${app.storage.firebase-bucket}")
    private String bucketName;

    @Value("${app.storage.type}")
    private String storageType;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        if ("FIREBASE".equalsIgnoreCase(storageType)) {
            try {
                if (configPath == null || configPath.trim().isEmpty()) {
                    log.warn("Firebase config path is empty. Storage will fallback to LOCAL.");
                    return;
                }
                FileInputStream serviceAccount = new FileInputStream(configPath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(bucketName)
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
                initialized = true;
                log.info("Firebase SDK successfully initialized.");
            } catch (IOException e) {
                log.error("Failed to initialize Firebase: {}. Falling back to LOCAL storage.", e.getMessage());
            }
        } else {
            log.info("Storage type configured to LOCAL. Local storage directories will be used.");
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
