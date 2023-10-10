package org.finalproject.TMeRoom.lecture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.lecture.controller.LectureController;
import org.finalproject.tmeroom.lecture.data.dto.request.AppointTeacherRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureCreateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureUpdateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.response.LectureDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.dto.response.StudentDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.dto.response.TeacherDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.service.LectureService;
import org.finalproject.tmeroom.lecture.service.StudentService;
import org.finalproject.tmeroom.lecture.service.TeacherService;
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

import static org.finalproject.TMeRoom.common.util.MockProvider.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LectureController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("강의 컨트롤러")
class LectureControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LectureService lectureService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private TeacherService teacherService;

    private Lecture getMockLecture() {
        return MockProvider.getMockLecture("강의명", getMockManagerMember());
    }

    private Teacher getMockTeacher() {
        return Teacher.builder()
                .member(getMockTeacherMember())
                .lecture(getMockLecture())
                .build();
    }


    private Student getMockStudent() {
        return Student.builder()
                .lecture(getMockLecture())
                .member(getMockStudentMember())
                .build();
    }

    @Test
    @DisplayName("강의 생성")
    void createLecture() throws Exception {
        // Given
        LectureCreateRequestDto requestDto = new LectureCreateRequestDto();
        requestDto.setLectureName("강의명");


        // When & Then
        mockMvc.perform(post("/api/v1/lecture")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDto))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("강의 수정")
    void updateLecture() throws Exception {
        // Given
        LectureUpdateRequestDto requestDto = new LectureUpdateRequestDto();
        requestDto.setLectureCode("code");
        requestDto.setLectureName("수정 된 강의명");

        // When & Then
        mockMvc.perform(put("/api/v1/lecture/code")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("강의 삭제")
    void deleteLecture() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("강의 강사 목록 조회")
    void readTeachers() throws Exception {
        // Given
        Teacher teacher = getMockTeacher();
        List<TeacherDetailResponseDto> dtoList = List.of(TeacherDetailResponseDto.from(teacher));
        Page<TeacherDetailResponseDto> responseDtoPage = new PageImpl<>(dtoList);

        given(teacherService.lookupTeachers(any(), any(), any())).willReturn(responseDtoPage);

        // When & Then
        mockMvc.perform(get("/api/v1/lecture/code/teachers")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].id").value(equalTo(dtoList.get(0).getId())))
                .andExpect(jsonPath("$.result.content[0].nickName").value(equalTo(dtoList.get(0).getNickName())))
                .andExpect(jsonPath("$.result.content[0].email").value(equalTo(dtoList.get(0).getEmail())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());

    }

    @Test
    @DisplayName("강의 강사 임명")
    void appointTeacher() throws Exception {
        // Given
        AppointTeacherRequestDto requestDto = new AppointTeacherRequestDto();
        requestDto.setTeacherId("teacher");

        // When & Then
        mockMvc.perform(post("/api/v1/lecture/code/teacher")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("강의 강사 해임")
    void dismissTeacher() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code/teacher")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());

    }

    @Test
    @DisplayName("강의 목록 조회")
    void lookupMyLectures() throws Exception {
        // Given
        Student student = getMockStudent();
        List<LectureDetailResponseDto> dtoList = List.of(LectureDetailResponseDto.from(student));
        Page<LectureDetailResponseDto> responseDtoPage = new PageImpl<>(dtoList);

        // When
        when(studentService.lookupMyLectures(any(), any())).thenReturn(responseDtoPage);

        // Then
        mockMvc.perform(get("/api/v1/lectures/taking")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.content[0].lectureCode").value(equalTo(dtoList.get(0).getLectureCode())))
                .andExpect(jsonPath("$.result.content[0].lectureName").value(equalTo(dtoList.get(0).getLectureName())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("수강 신청")
    void applyLecture() throws Exception {
        // Given


        // When & Then
        mockMvc.perform(post("/api/v1/lecture/code/application")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("수강 신청 철회")
    void cancelApplication() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code/application")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("수강 신청 인원 목록 조회")
    void readStudents() throws Exception {
        // Given
        Student student = getMockStudent();
        List<StudentDetailResponseDto> dtoList = List.of(StudentDetailResponseDto.from(student));
        Page<StudentDetailResponseDto> responseDtoPage = new PageImpl<>(dtoList);

        // When
        when(studentService.checkApplicants(any(), any(), any())).thenReturn(responseDtoPage);

        // Then
        mockMvc.perform(get("/api/v1/lecture/code/applications")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].memberNickname").value(
                        equalTo(dtoList.get(0).getMemberNickname())))
                .andExpect(jsonPath("$.result.content[0].lectureCode").value(equalTo(dtoList.get(0).getLectureCode())))
                .andExpect(jsonPath("$.result.content[0].lectureName").value(equalTo(dtoList.get(0).getLectureName())))
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("수강 신청 수락")
    void acceptApplicant() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(put("/api/v1/lecture/code/application/0")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }

    @Test
    @DisplayName("수강 신청 반려")
    void rejectApplicant() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(delete("/api/v1/lecture/code/application/0")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
    }
}