package org.finalproject.tmeroom.file.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.file.data.dto.response.FileDetailResponseDto;
import org.finalproject.tmeroom.file.service.FileService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    // 파일 등록
    @PostMapping("/lecture/{lectureCode}/file")
    public Response<Void> registerFile(@PathVariable String lectureCode,
                                       @AuthenticationPrincipal MemberDto memberDto,
                                       @RequestPart("file") List<MultipartFile> multipartFiles)
            throws Exception {
        fileService.registerFile(lectureCode, memberDto, multipartFiles);

        return Response.success();
    }


    // 파일 삭제
    @DeleteMapping("/lecture/{lectureCode}/file/{fileId}")
    public Response<Void> deleteFile(@PathVariable String lectureCode,
                                     @PathVariable Long fileId,
                                     @AuthenticationPrincipal MemberDto memberDto) {
        fileService.deleteFile(lectureCode, fileId, memberDto);

        return Response.success();
    }

    // 파일 검색
    @GetMapping("/lecture/{lectureCode}/file")
    public Response<Page<FileDetailResponseDto>> searchFiles(@PathVariable String lectureCode,
                                                             @RequestParam("type") String fileType,
                                                             @RequestParam("key") String keyword,
                                                             @AuthenticationPrincipal MemberDto memberDto,
                                                             @PageableDefault Pageable pageable) {
        Page<FileDetailResponseDto> responseDto =
                fileService.findFilesByKeywordAndLecture(keyword, lectureCode, fileType, pageable, memberDto);

        return Response.success(responseDto);
    }
}
