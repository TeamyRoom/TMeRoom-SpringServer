package org.finalproject.tmeroom.file.data.dto.response;

import lombok.Getter;
import org.finalproject.tmeroom.file.data.entity.File;

import java.util.List;

@Getter
public class FileUploadResponseDto {

    private List<FileDetailResponseDto> uploadedFiles;

    private FileUploadResponseDto(List<File> files) {
        this.uploadedFiles = files.stream()
                .map(FileDetailResponseDto::from)
                .toList();
    }

    public static FileUploadResponseDto from(List<File> files) {
        return new FileUploadResponseDto(files);
    }
}
