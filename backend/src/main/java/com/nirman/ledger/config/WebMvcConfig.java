package com.nirman.ledger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.storage.local-dir:./uploads}")
    private String localDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(localDir).toAbsolutePath().normalize();
        String uploadPath = uploadDir.toString().replace("\\", "/");

        // Windows absolute paths might need file:/// or file: prefixes
        String filePrefix = "file:";
        if (!uploadPath.startsWith("/")) {
            filePrefix = "file:/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(filePrefix + uploadPath + "/");
    }
}
