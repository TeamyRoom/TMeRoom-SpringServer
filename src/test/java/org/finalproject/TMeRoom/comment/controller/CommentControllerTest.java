package org.finalproject.TMeRoom.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.comment.controller.CommentController;
import org.finalproject.tmeroom.comment.data.dto.request.CommentCreateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.request.CommentUpdateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.response.CommentDetailResponseDto;
import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.comment.service.CommentService;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.finalproject.TMeRoom.common.util.MockProvider.getMockManagerMember;
import static org.finalproject.TMeRoom.common.util.MockProvider.getMockStudentMember;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@WithMockUser
@DisplayName("댓글 컨트롤러")
class CommentControllerTest {
    private static final Long MOCK_ID = 1L;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService commentService;

    public Lecture getMockLecture() {
        return MockProvider.getMockLecture("강의명", getMockManagerMember());
    }

    public Question getMockQuestion() {
        return Question.builder()
                .id(MOCK_ID)
                .lecture(getMockLecture())
                .author(getMockStudentMember())
                .title("title")
                .content("content")
                .authorNickname("author")
                .isPublic(false)
                .build();
    }

    private Comment getMockComment() {
        return Comment.builder()
                .id(MOCK_ID)
                .commenter(getMockStudentMember())
                .content("content")
                .question(getMockQuestion())
                .build();
    }

    @Test
    @DisplayName("댓글 조회")
    void readComments() throws Exception {
        // Given
        Comment comment = getMockComment();
        List<CommentDetailResponseDto> dtoList = List.of(CommentDetailResponseDto.from(comment));
        Page<CommentDetailResponseDto> responseDtoPage = new PageImpl<>(dtoList);

        // When
        when(commentService.readComments(any(), any())).thenReturn(responseDtoPage);

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/question/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content[0].commentId").value(
                        equalTo(dtoList.get(0).getCommentId().intValue())))
                .andExpect(jsonPath("$.result.content[0].content").value(equalTo(dtoList.get(0).getContent())))
                .andExpect(jsonPath("$.result.content[0].commenterNickname").value(
                        equalTo(dtoList.get(0).getCommenterNickname())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("댓글 생성")
    void createComment() throws Exception {
        // Given
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto();
        requestDto.setContent("content");

        // When & Then
        mockMvc.perform(post("/api/v1/lecture/code/question/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        // Given
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto();
        requestDto.setContent("content");

        // When & Then
        mockMvc.perform(put("/api/v1/lecture/code/question/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code/question/1/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }
}