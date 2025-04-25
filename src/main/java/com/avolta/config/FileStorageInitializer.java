package com.avolta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileStorageInitializer implements CommandLineRunner {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void run(String... args) throws Exception {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + uploadDir);
            } else {
                System.err.println("Failed to create directory: " + uploadDir);
            }
        }
    }
}