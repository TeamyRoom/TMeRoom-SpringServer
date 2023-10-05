package org.finalproject.tmeroom.file.data.dto.response;

import lombok.Builder;
import lombok.Data;
import org.finalproject.tmeroom.file.data.entity.File;

@Data
public class FileDetailResponseDto {
    String fileName;
    String fileLink;
    String fileUploaderNickname;

    @Builder
    private FileDetailResponseDto(String fileName, String fileLink, String fileUploaderNickname) {
        this.fileName = fileName;
        this.fileLink = fileLink;
        this.fileUploaderNickname = fileUploaderNickname;
    }

    public static FileDetailResponseDto from(File file) {
        return FileDetailResponseDto.builder()
                .fileLink(file.getFileLink())
                .fileName(file.getFileName())
                .fileUploaderNickname(file.getUploaderNickname())
                .build();
    }
}
