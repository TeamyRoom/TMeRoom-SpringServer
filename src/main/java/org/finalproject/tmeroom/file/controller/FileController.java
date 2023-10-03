package org.finalproject.tmeroom.file.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.file.data.dto.response.FileDetailResponseDto;
import org.finalproject.tmeroom.file.data.dto.response.FileUploadResponseDto;
import org.finalproject.tmeroom.file.service.FileService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 파일 조회
    @GetMapping("/lecture/{lectureCode}/files")
    public Response<Page<FileDetailResponseDto>> readFiles(@PathVariable String lectureCode,
                                                           @AuthenticationPrincipal MemberDto memberDto,
                                                           @PageableDefault(sort = "createdAt",
                                                                   direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        Page<FileDetailResponseDto> dtoPage = fileService.lookupFiles(lectureCode, memberDto, pageable);

        return Response.success(dtoPage);
    }

    // 파일 등록
    @PostMapping("/lecture/{lectureCode}/file")
    public Response<Void> registerFile(@PathVariable String lectureCode,
                                                        @AuthenticationPrincipal MemberDto memberDto,
                                                        @RequestPart("file") List<MultipartFile> multipartFiles)
            throws Exception {
        fileService.registerFile(lectureCode, memberDto, multipartFiles);

        return Response.success();
    }

    //파일 다운로드
    @GetMapping("/lecture/{lectureCode}/file/{fileId}")
    public Response<UrlResource> downloadFile(@PathVariable String lectureCode,
                                              @PathVariable Long fileId,
                                              @AuthenticationPrincipal MemberDto memberDto,
                                              HttpServletResponse response) {

        UrlResource urlResource = fileService.getUrlResource(lectureCode, fileId, memberDto);
        String contentDisposition = "attachment; filename=\"" + urlResource.getFilename() + "\"";
        response.addHeader(CONTENT_DISPOSITION, contentDisposition);

        return Response.success(urlResource);
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
    @GetMapping("/lecture/{lectureCode}/file/search")
    public Response<Page<FileDetailResponseDto>> searchFiles(@PathVariable String lectureCode,
                                                             @RequestParam String keyword,
                                                             @AuthenticationPrincipal MemberDto memberDto,
                                                             @PageableDefault(sort = "createdAt",
                                                                     direction = Sort.Direction.DESC)
                                                             Pageable pageable) {
        Page<FileDetailResponseDto> responseDto =
                fileService.findFilesByKeywordAndLecture(keyword, lectureCode, pageable);

        return Response.success(responseDto);
    }
}
