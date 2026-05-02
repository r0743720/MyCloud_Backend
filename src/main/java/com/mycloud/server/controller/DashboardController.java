package com.mycloud.server.controller;

import com.mycloud.server.dto.DuplicateDTO;
import com.mycloud.server.dto.StorageStatsResponseDTO;
import com.mycloud.server.service.FileService;
import com.mycloud.server.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final StorageService storageService;

    @GetMapping("/storage")
    public StorageStatsResponseDTO storageStats(@AuthenticationPrincipal String username){
        return storageService.getStats(username);
    }

    @GetMapping("/duplicates")
    public List<DuplicateDTO> duplicates(@AuthenticationPrincipal String username){
        return storageService.getDuplicates(username);
    }
}
