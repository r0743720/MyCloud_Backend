package com.mycloud.server.service;

import com.mycloud.server.config.StorageConfig;
import com.mycloud.server.model.File;
import com.mycloud.server.model.User;
import com.mycloud.server.repository.FileRepository;
import com.mycloud.server.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final StorageConfig storageConfig;

    @CacheEvict(value = "storageStats", key = "#username")
    public File upload(MultipartFile file, String username) throws IOException {
        User owner = getUser(username);

        // Check quota — skip for ADMIN
        if (owner.getRole() != User.Role.ADMIN) {
            Long used = fileRepository.sumSizeByOwner(owner);
            long usedBytes = used != null ? used : 0L;
            if (usedBytes + file.getSize() > owner.getStorageQuotaBytes()) {
                throw new ResponseStatusException(
                        HttpStatus.CONTENT_TOO_LARGE,
                        "Storage quota exceeded. Used: " + usedBytes +
                                " bytes, quota: " + owner.getStorageQuotaBytes() + " bytes."
                );
            }
        }
        // Generate a unique filename to prevent collisions on disk
        String extension = getExtension(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path destination = storageConfig.getStoragePath().resolve(storedName);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        File newFile = new File();
        newFile.setFileName(file.getOriginalFilename());
        newFile.setFilePath(destination.toString());
        newFile.setFileSize(file.getSize());
        newFile.setFileType(extension.isEmpty() ? "unknown" : extension.toLowerCase()); // 👈 this was missing
        newFile.setMimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        newFile.setOwner(owner);

        return fileRepository.save(newFile);
    }

    public List<File> listFiles(String username){
        User owner = getUser(username);
        if(owner.getRole() == User.Role.ADMIN){
            return fileRepository.findAllFiles();
        }
        return fileRepository.findByOwner(getUser(username));
    }

    public Resource download(Long fileId, String username) throws MalformedURLException {
        File file = getOwnedFile(fileId, username);
        Path path = Path.of(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not readable on disk");
        }
        return resource;
    }

    @CacheEvict(value = "storageStats", key = "#username")
    public File rename(Long fileId, String newName, String username) {
        File file = getOwnedFile(fileId, username);
        file.setFileName(newName);
        return fileRepository.save(file); // trigger fires here
    }

    @CacheEvict(value = "storageStats", key = "#username")
    public void delete(Long fileId, String username) throws IOException {
        File file = getOwnedFile(fileId, username);
        Files.deleteIfExists(Path.of(file.getFileName()));
        fileRepository.delete(file); // trigger fires here
    }

    public List<File> getDuplicateCandidates(String username) {
        return fileRepository.findDuplicateCandidatesByOwner(getUser(username));
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    private File getOwnedFile(Long fileId, String username) {
        return fileRepository.findByIdAndOwner(fileId, getUser(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }


}
