package com.example.WebKtx.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root = Paths.get("uploads/announcements"); // thư mục local

    public FileStorageService() {
        try { Files.createDirectories(root); } catch (IOException ignored) {}
    }

    public String saveAnnouncementImage(MultipartFile file) {
        if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file");
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf('.')))
                .orElse("");
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot save file");
        }
        // URL public (xem ResourceHandler bên dưới)
        return "/uploads/announcements/" + filename;
    }
}

