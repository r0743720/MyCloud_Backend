package com.mycloud.server.controller;

import com.mycloud.server.model.File;
import com.mycloud.server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<File> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String username) throws IOException {
        return ResponseEntity.ok(fileService.upload(file, username));
     }

    @GetMapping
    public List<File> list(@AuthenticationPrincipal String username) {
        return fileService.listFiles(username);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) throws IOException {
        Resource resource = fileService.download(id, username);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename()+ "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @PutMapping("/{id}")
    public ResponseEntity<File> rename(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(fileService.rename(id, body.get("filename"), username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal String username) throws IOException {
        fileService.delete(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/duplicates")
    public List<File> duplicates (@AuthenticationPrincipal String username){
        return fileService.getDuplicateCandidates(username);
    }
}
