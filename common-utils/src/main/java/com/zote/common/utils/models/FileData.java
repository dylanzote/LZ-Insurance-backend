package com.zote.common.utils.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileData {
    private String filename;
    private String fileType;
    private long fileSize;
    private byte[] file;
}
