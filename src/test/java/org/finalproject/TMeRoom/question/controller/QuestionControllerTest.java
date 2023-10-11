package org.finalproject.TMeRoom.question.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.question.controller.QuestionController;
import org.finalproject.tmeroom.question.data.dto.request.QuestionCreateRequestDto;
import org.finalproject.tmeroom.question.data.dto.request.QuestionUpdateRequestDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionDetailResponseDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionListResponseDto;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.finalproject.tmeroom.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.finalproject.TMeRoom.common.util.MockProvider.getMockManagerMember;
import static org.finalproject.TMeRoom.common.util.MockProvider.getMockStudentMember;
import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("질문 컨트롤러")
class QuestionControllerTest {
    private static final Long MOCK_QUESTION_ID = 1L;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private QuestionService questionService;

    public Lecture getMockLecture() {
        return MockProvider.getMockLecture("강의명", getMockManagerMember());
    }

    public Question getMockQuestion() {
        return Question.builder()
                .id(MOCK_QUESTION_ID)
                .lecture(getMockLecture())
                .author(getMockStudentMember())
                .title("title")
                .content("content")
                .authorNickname("author")
                .isPublic(false)
                .build();
    }

    @Test
    @DisplayName("정상적인 요청이라면, 전체 질문 조회시, 성공 코드와 전체 질문을 반환한다.")
    void successLookupAllQuestions() throws Exception {
        // Given
        Question question = getMockQuestion();
        List<QuestionListResponseDto> dtoList = List.of(QuestionListResponseDto.from(question));
        Page<QuestionListResponseDto> responseDtoPage = new PageImpl<>(dtoList);

        // When
        when(questionService.lookupAllQuestions(any(), any(), any())).thenReturn(responseDtoPage);

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content[0].questionId").value(
                        equalTo(dtoList.get(0).getQuestionId().intValue())))
                .andExpect(
                        jsonPath("$.result.content[0].questionTitle").value(equalTo(dtoList.get(0).getQuestionTitle())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());

    }

    @Test
    @DisplayName("정상적인 요청이라면, 공개 질문 조회시, 성공 코드와 공개 질문을 반환한다.")
    void successLookupPublicQuestions() throws Exception {
        // Given
        Question question = getMockQuestion();
        List<QuestionListResponseDto> dtoList = List.of(QuestionListResponseDto.from(question));
        Page<QuestionListResponseDto> responseDtoPage = new PageImpl<>(dtoList);

        // When
        when(questionService.lookupPublicQuestions(any(), any(), any())).thenReturn(responseDtoPage);

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/questions/permitted-only")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content[0].questionId").value(
                        equalTo(dtoList.get(0).getQuestionId().intValue())))
                .andExpect(
                        jsonPath("$.result.content[0].questionTitle").value(equalTo(dtoList.get(0).getQuestionTitle())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());

    }

    @Test
    @DisplayName("정상적인 요청이라면, 단일 질문 조회시, 성공 코드와 단일 질문 내용을 반환한다.")
    void successReadQuestion() throws Exception {
        // Given
        Question question = getMockQuestion();
        QuestionDetailResponseDto responseDto = QuestionDetailResponseDto.from(question);

        // When
        when(questionService.readQuestion(any(), any(), any())).thenReturn(responseDto);

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/question/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.questionId").value(equalTo(responseDto.getQuestionId().intValue())))
                .andExpect(jsonPath("$.result.questionTitle").value(equalTo(responseDto.getQuestionTitle())))
                .andExpect(jsonPath("$.result.questionContent").value(equalTo(responseDto.getQuestionContent())))
                .andExpect(jsonPath("$.result.authorNickname").value(equalTo(responseDto.getAuthorNickname())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("없는 질문 번호라면, 단일 질문 조회시, 실패 코드를 반환한다.")
    void failReadQuestion() throws Exception {
        // Given
        Question question = getMockQuestion();
        QuestionDetailResponseDto responseDto = QuestionDetailResponseDto.from(question);

        // When
        when(questionService.readQuestion(any(), any(), any())).thenThrow(
                new ApplicationException(ErrorCode.INVALID_QUESTION_ID));

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/question/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());

    }

    @Test
    @DisplayName("정상적인 요청이라면, 질문 생성 요청시, 성공 코드를 반환한다.")
    void successCreateQuestion() throws Exception {
        // Given
        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto();
        requestDto.setTitle("질문 제목");
        requestDto.setContent("질문 내용");
        requestDto.setIsPublic(false);

        // When & Then
        mockMvc.perform(post("/api/v1/lecture/code/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("질문 제목이 비어있다면, 질문 생성 요청시, 실패 코드를 반환한다.")
    void failCreateQuestion() throws Exception {
        // Given
        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto();
        requestDto.setContent("질문 내용");
        requestDto.setIsPublic(false);

        // When & Then
        mockMvc.perform(post("/api/v1/lecture/code/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result[0].message").value(equalTo(CANNOT_BE_NULL)));
    }

    @Test
    @DisplayName("정상적인 요청이라면, 질문 수정 요청시, 성공 코드를 반환한다.")
    void successUpdateQuestion() throws Exception {
        // Given
        QuestionUpdateRequestDto requestDto = new QuestionUpdateRequestDto();
        requestDto.setTitle("질문 제목");
        requestDto.setContent("질문 내용");
        requestDto.setIsPublic(false);

        // When & Then
        mockMvc.perform(put("/api/v1/lecture/code/question/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("수정된 질문 제목이 비어있다면, 질문 생성 요청시, 실패 코드를 반환한다.")
    void failUpdateQuestion() throws Exception {
        // Given
        QuestionUpdateRequestDto requestDto = new QuestionUpdateRequestDto();
        requestDto.setContent("질문 내용");
        requestDto.setIsPublic(false);

        // When & Then
        mockMvc.perform(put("/api/v1/lecture/code/question/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result[0].message").value(equalTo(CANNOT_BE_NULL)));
    }

    @Test
    @DisplayName("정상적인 요청이라면, 질문 삭제 요청시, 성공 코드를 반환한다.")
    void successDeleteQuestion() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code/question/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("정상적인 요청이라면, 질문 공개 요청시, 성공 코드를 반환한다.")
    void successOpenQuestion() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(put("/api/v1/lecture/code/question/1/public")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }
}