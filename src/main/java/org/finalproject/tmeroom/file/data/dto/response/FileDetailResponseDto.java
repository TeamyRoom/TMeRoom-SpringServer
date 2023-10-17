package org.finalproject.tmeroom.file.data.dto.response;

import lombok.Builder;
import lombok.Data;
import org.finalproject.tmeroom.file.data.entity.File;

@Data
public class FileDetailResponseDto {
    private Long fileId;
    private String fileName;
    private String fileLink;
    private String fileUploaderNickname;

    @Builder
    private FileDetailResponseDto(Long fileId, String fileName, String fileLink, String fileUploaderNickname) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileLink = fileLink;
        this.fileUploaderNickname = fileUploaderNickname;
    }

    public static FileDetailResponseDto from(File file) {
        return FileDetailResponseDto.builder()
                .fileId(file.getId())
                .fileLink(file.getFileLink())
                .fileName(file.getFileName())
                .fileUploaderNickname(file.getUploaderNickname())
                .build();
    }
}
