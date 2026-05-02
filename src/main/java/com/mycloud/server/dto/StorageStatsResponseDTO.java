package com.mycloud.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class StorageStatsResponseDTO {
    private long totalBytes;
    private long usedBytes;
    private long freeBytes;
    private int totalFiles;

    private Map<String, Long> byFileType;
    private Map<String, Long> bytesByFileType;
}
