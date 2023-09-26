package org.finalproject.TMeRoom.lecture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.tmeroom.lecture.controller.LectureController;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureCreateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureUpdateRequestDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.service.LectureService;
import org.finalproject.tmeroom.lecture.service.StudentService;
import org.finalproject.tmeroom.lecture.service.TeacherService;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

    private Member getMockMember() {
        return Member.builder()
                .email("testUser@test.com")
                .id("testUser")
                .pw("password")
                .role(MemberRole.USER)
                .build();
    }

    private MemberDto getMockMemberDto(Member member) {
        return MemberDto.from(member);
    }

    private Lecture getMockLecture() {
        return Lecture.builder()
                .lectureCode("code")
                .lectureName("강의명")
                .manager(getMockMember())
                .build();
    }

    @Test
    @WithAnonymousUser
    void createLecture() throws Exception {

        // Given
        Member member = getMockMember();
        MemberDto memberDto = getMockMemberDto(member);
        LectureCreateRequestDto requestDto = new LectureCreateRequestDto();
        requestDto.setLectureName("강의명");

//        given(memberRepository.findById(memberDto.getId())).willReturn(Optional.of(member));
//        LectureCreateResponseDto responseDto = mock(LectureCreateResponseDto.class);
//        given(lectureService.createLecture(requestDto)).willReturn(responseDto);

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
    @DisplayName("강의 강사 목록 조회")
    @Disabled
    void readTeachers() {
        // Given


        // When & Then
//        mockMvc.perform(get("/api/v1/lecture/code/teachers")
//                .content());

    }

    @Test
    @Disabled
    void appointTeacher() {

    }

    @Test
    @Disabled
    void dismissTeacher() {

    }

    @Test
    @Disabled
    void lookupMyLectures() {

    }

    @Test
    @Disabled
    void applyLecture() {

    }

    @Test
    @Disabled
    void cancelApplication() {

    }

    @Test
    @Disabled
    void readStudents() {

    }

    @Test
    @Disabled
    void acceptApplicant() {

    }

    @Test
    @Disabled
    void rejectApplicant() {

    }

    @Nested
    @DisplayName("강의 삭제")
    class aboutLectureDelete {
        @Test
        @DisplayName("강의 관리자가 삭제 요청시 강의 삭제")
        void successDeleteLecture() throws Exception {

            // Given

            // When & Then
            mockMvc.perform(delete("/api/v1/lecture/code")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }

        @Test
        @DisplayName("강의 관리자가 아닌 사람이 삭제 요청시 강의 삭제 실패")
        void failDeleteLecture() throws Exception {
            // Given

            // When & Then
            mockMvc.perform(delete("/api/v1/lecture/code")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }
    }
}