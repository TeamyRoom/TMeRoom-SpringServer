package org.finalproject.TMeRoom.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.tmeroom.file.controller.FileController;
import org.finalproject.tmeroom.file.data.dto.response.FileDetailResponseDto;
import org.finalproject.tmeroom.file.data.entity.File;
import org.finalproject.tmeroom.file.data.entity.FileType;
import org.finalproject.tmeroom.file.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.finalproject.TMeRoom.common.util.MockProvider.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@WithMockUser
@DisplayName("파일 컨트롤러")
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FileService fileService;

    private static final String MOCK_LECTURE_CODE = "code";
    private static final Pageable MOCK_PAGEABLE = PageRequest.of(0, 20);

    private File getMockFile() {
        return File.builder()
                .fileLink("localhost")
                .lecture(getMockLecture("강의명", getMockManagerMember()))
                .uploaderNickname(getMockTeacherMember().getNickname())
                .fileName("test")
                .fileType(FileType.CODE)
                .uuidFileName("uuidFileName")
                .build();
    }

    private MockMultipartFile getMockMultiPartFile(){
        return new MockMultipartFile("file", "test.txt", "text/plain", "test file".getBytes(UTF_8) );
    }

    @Test
    @DisplayName("파일 등록")
    void registerFile() throws Exception {
        // Given
        MockMultipartFile mockMultiPartFile = getMockMultiPartFile();

        // When & Then
        mockMvc.perform(multipart("/api/v1/lecture/code/file")
                        .file(mockMultiPartFile)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());

    }

    @Test
    @DisplayName("파일 삭제")
    void deleteFile() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code/file/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("파일 검색")
    void searchFiles() throws Exception {
        // Given
        File mockFile = getMockFile();
        Page<FileDetailResponseDto> responseDtoPage = new PageImpl<>(List.of(FileDetailResponseDto.from(mockFile)));
        String fileType = FileType.CODE.name();
        String keyword = mockFile.getFileName();

        // When
        when(fileService.findFilesByKeywordAndLecture(any(),any(),any(), any(), any())).thenReturn(responseDtoPage);

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/file")
                        .param("type", fileType)
                        .param("key", keyword)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content[0].fileName").value(
                        equalTo(responseDtoPage.stream().findFirst().get().getFileName())))
                .andExpect(jsonPath("$.result.content[0].fileLink").value(
                        equalTo(responseDtoPage.stream().findFirst().get().getFileLink())))
                .andExpect(jsonPath("$.result.content[0].fileUploaderNickname").value(
                        equalTo(responseDtoPage.stream().findFirst().get().getFileUploaderNickname())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }
}