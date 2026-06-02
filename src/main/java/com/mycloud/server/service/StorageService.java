package com.mycloud.server.service;

import com.mycloud.server.dto.DuplicateDTO;
import com.mycloud.server.dto.StorageStatsResponseDTO;
import com.mycloud.server.model.File;
import com.mycloud.server.model.User;
import com.mycloud.server.repository.FileRepository;
import com.mycloud.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String stripExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }

    @Cacheable(value = "storageStats", key = "#username")
    public StorageStatsResponseDTO getStats(String username) {
        User owner = getUser(username);

        if (owner.getRole() == User.Role.ADMIN) {
            // Server-wide stats
            long usedBytes = fileRepository.sumAllSizes();
            long quota = 0; // no quota for admin
            int totalFiles = (int) fileRepository.count();

            Map<String, Long> byFileType = new LinkedHashMap<>();
            for (Object[] row : fileRepository.countAllByFileType()) {
                String type = row[0] != null ? (String) row[0] : "unknown";
                byFileType.put(type, (Long) row[1]);
            }

            Map<String, Long> bytesByFileType = new LinkedHashMap<>();
            for (Object[] row : fileRepository.sumAllBytesByFileType()) {
                String type = row[0] != null ? (String) row[0] : "unknown";
                bytesByFileType.put(type, (Long) row[1]);
            }

            return new StorageStatsResponseDTO(
                    quota,
                    usedBytes,
                    0,
                    totalFiles,
                    byFileType,
                    bytesByFileType
            );
        }
        long usedBytes = Optional.ofNullable(
                fileRepository.sumSizeByOwner(owner)).orElse(0L);
        long quota = owner.getStorageQuotaBytes();
        int totalFiles = (int) fileRepository.countByOwner(owner);

        Map<String, Long> byFileType = new LinkedHashMap<>();
        for (Object[] row : fileRepository.countByFileType(owner)) {
            String type = row[0] != null ? (String) row[0] : "unknown";
            byFileType.put(type, (Long) row[1]);
        }
        Map<String, Long> bytesByFileType = new LinkedHashMap<>();
        for (Object[] row : fileRepository.sumBytesByFileType(owner)) {
            String type = row[0] != null ? (String) row[0] : "unknown";
            bytesByFileType.put(type, (Long) row[1]);
        }

        return new StorageStatsResponseDTO(
                quota,
                usedBytes,
                quota - usedBytes,
                totalFiles,
                byFileType,
                bytesByFileType
        );
    }
    public List<DuplicateDTO> getDuplicates(String username) {
        User user = getUser(username);
        List<File> allFiles = fileRepository.findAllByOwnerOrderByFileName(user);
        Pattern pattern = Pattern.compile("^(.+?)\\s*\\(\\d+\\)(\\.[^.]+)?$");

        Map<String, List<File>> groups = new LinkedHashMap<>();
        for (File file : allFiles) {
            String nameWithoutExt = stripExtension(file.getFileName());
            Matcher matcher = pattern.matcher(nameWithoutExt);

            String baseName = matcher.matches() ? matcher.group(1).trim() : nameWithoutExt;
            groups.computeIfAbsent(baseName, k -> new ArrayList<>()).add(file);
        }

        List<DuplicateDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<File>> entry : groups.entrySet()) {
            List<File> copies = entry.getValue();
            if (copies.size() < 2) continue;
            long wastedByes = copies.stream()
                    .skip(1)
                    .mapToLong(File::getFileSize)
                    .sum();
            List<DuplicateDTO.DuplicateFile> dtoFiles = copies.stream()
                    .map(f -> new DuplicateDTO.DuplicateFile(
                            (long) f.getId(),
                            f.getFileName(),
                            f.getFileSize(),
                            f.getFileType()
                    ))
                    .collect(Collectors.toList());
            result.add(new DuplicateDTO(
                    entry.getKey(),
                    copies.size(),
                    wastedByes,
                    dtoFiles
            ));
        }
        return result;
    }

}
