package com.mycloud.server.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class StorageConfig {
    @Value("${storage.path}")
    private String storagePath;

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(storagePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println("Create directory: " + path.toAbsolutePath());
        }
    }
    public Path getStoragePath() {
        return Paths.get(storagePath);
    }
}
