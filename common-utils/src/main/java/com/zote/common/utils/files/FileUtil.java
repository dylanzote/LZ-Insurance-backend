package com.zote.common.utils.files;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.models.FileData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FileUtil {

    public void checkFileSize(MultipartFile file, long maxFileUploadSize) {
        if (file.getSize() > maxFileUploadSize) {
            throw new FunctionalError("File size is too large");
        }
    }

    public void checkFileIsNotNull(MultipartFile file) {
        if (file == null) {
            throw new FunctionalError("File is null");
        }
        if (StringUtils.isEmpty(file.getName())) {
            throw new FunctionalError("File name is empty");
        }
    }

    @SneakyThrows
    public FileData createFile(MultipartFile file, long maxFileUploadSize) {
        checkFileSize(file, maxFileUploadSize);
        checkFileIsNotNull(file);
        return FileData.builder()
                .file(file.getBytes())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filename(file.getOriginalFilename())
                .build();
    }

    public List<FileData> createFileDataList(List<MultipartFile> file, long maxFileUploadSize) {
        return file.stream().map(fileStream -> {
            checkFileSize(fileStream, maxFileUploadSize);
            checkFileIsNotNull(fileStream);
            try {
                return FileData.builder()
                        .file(fileStream.getBytes())
                        .fileType(fileStream.getContentType())
                        .fileSize(fileStream.getSize())
                        .filename(fileStream.getOriginalFilename())
                        .build();
            } catch (IOException e) {
                throw new FunctionalError(e.getMessage());
            }
        }).toList();
    }


}
