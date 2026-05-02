package com.mycloud.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DuplicateDTO {
    private String baseName;
    private int count;
    private long wastedBytes;
    private List<DuplicateFile> files;

    @Data
    @AllArgsConstructor
    public static class DuplicateFile{
        private Long id;
        private String fileName;
        private long sizeBytes;
        private String fileType;
    }
}
