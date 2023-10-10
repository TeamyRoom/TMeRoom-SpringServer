package org.finalproject.TMeRoom.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.tmeroom.admin.constant.LectureSearchType;
import org.finalproject.tmeroom.admin.constant.MemberSearchType;
import org.finalproject.tmeroom.admin.controller.AdminController;
import org.finalproject.tmeroom.admin.data.dto.request.AdminLectureSearchRequestDto;
import org.finalproject.tmeroom.admin.data.dto.request.AdminMemberDetailProfileRequestDto;
import org.finalproject.tmeroom.admin.data.dto.request.AdminMemberSearchRequestDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLecturePageReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberPageReadResponseDto;
import org.finalproject.tmeroom.admin.service.AdminService;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.finalproject.TMeRoom.common.util.MockMemberProvider.getMockManagerMember;
import static org.finalproject.TMeRoom.common.util.MockMemberProvider.getMockUserMember;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@WithMockUser
@DisplayName("회원 관련 컨트롤러 테스트")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AdminService adminService;

    private Lecture getMockLecture() {
        return Lecture.builder()
                .lectureCode("mockLectureCode")
                .lectureName("mockLectureName")
                .manager(getMockManagerMember())
                .build();
    }

    @Nested
    @DisplayName("회원 검색 관련 테스트")
    class aboutSearchingMembers {

        @Test
        @DisplayName("올바른 요청일 때, 회원 검색 요청을 보내면, 성공 코드와 함께 회원 검색 결과 페이지를 반환한다.")
        void givenProperRequest_whenSearchingMembers_thenReturnsSearchedMembersPage() throws Exception {
            // Given
            MemberSearchType mockSearchType = mock(MemberSearchType.class);
            given(mockSearchType.name()).willReturn(MemberSearchType.ID.name());
            String mockKeyword = "mockKeyword";

            Member mockMember = getMockUserMember();
            AdminMemberPageReadResponseDto responseDto =
                    AdminMemberPageReadResponseDto.of(new PageImpl<>(List.of(mockMember)));

            given(adminService.searchMembers(any(AdminMemberSearchRequestDto.class), any())).willReturn(
                    responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/members")
                            .queryParam("keyword", mockKeyword)
                            .queryParam("searchType", mockSearchType.name())
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists())
                    .andExpect(jsonPath("$.result.members.content").exists());
        }

        @Test
        @DisplayName("잘못된 검색 타입일 때, 회원 검색 요청을 보내면, 실패 코드와 함께 잘못된 파라미터 이름을 보낸다.")
        void givenInvalidSearchType_whenSearchingMembers_thenReturnsFailedParameterName() throws Exception {
            // Given
            MemberSearchType mockSearchType = mock(MemberSearchType.class);
            given(mockSearchType.name()).willReturn("wrongSearchType");
            String mockKeyword = "mockKeyword";

            Member mockMember = getMockUserMember();
            AdminMemberPageReadResponseDto responseDto =
                    AdminMemberPageReadResponseDto.of(new PageImpl<>(List.of(mockMember)));

            given(adminService.searchMembers(any(AdminMemberSearchRequestDto.class), any())).willReturn(
                    responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/members")
                            .queryParam("keyword", mockKeyword)
                            .queryParam("searchType", mockSearchType.name())
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }
    }

    @Nested
    @DisplayName("강의 검색 관련 테스트")
    class aboutSearchingLectures {

        @Test
        @DisplayName("올바른 요청일 때, 강의 검색 요청을 보내면, 성공 코드와 함께 강의 검색 결과 페이지를 반환한다.")
        void givenProperRequest_whenSearchingLectures_thenReturnsSearchedLecturesPage() throws Exception {
            // Given
            LectureSearchType mockSearchType = mock(LectureSearchType.class);
            given(mockSearchType.name()).willReturn(LectureSearchType.NAME.name());
            String mockKeyword = "mockKeyword";

            Lecture mockLecture = getMockLecture();
            AdminLecturePageReadResponseDto responseDto =
                    AdminLecturePageReadResponseDto.of(new PageImpl<>(List.of(mockLecture)));

            given(adminService.searchLectures(any(AdminLectureSearchRequestDto.class), any())).willReturn(
                    responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/lectures")
                            .queryParam("keyword", mockKeyword)
                            .queryParam("searchType", mockSearchType.name())
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists())
                    .andExpect(jsonPath("$.result.lectures.content").exists());
        }

        @Test
        @DisplayName("잘못된 검색 타입일 때, 강의 검색 요청을 보내면, 실패 코드와 함께 잘못된 파라미터 이름을 보낸다.")
        void givenInvalidSearchType_whenSearchingLectures_thenReturnsFailedParameterName() throws Exception {
            // Given
            LectureSearchType mockSearchType = mock(LectureSearchType.class);
            given(mockSearchType.name()).willReturn("wrongSearchType");
            String mockKeyword = "mockKeyword";

            Lecture mockLecture = getMockLecture();
            AdminLecturePageReadResponseDto responseDto =
                    AdminLecturePageReadResponseDto.of(new PageImpl<>(List.of(mockLecture)));

            given(adminService.searchLectures(any(AdminLectureSearchRequestDto.class), any())).willReturn(
                    responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/lectures")
                            .queryParam("keyword", mockKeyword)
                            .queryParam("searchType", mockSearchType.name())
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }
    }

    @Nested
    @DisplayName("회원 프로필 정보 조회 기능 테스트")
    class aboutReadingMemberDetailProfile {

        @Test
        @DisplayName("올바른 요청일 때, 회원 정보 조회 요청을 보내면, 성공 코드와 함께 회원 정보를 반환한다.")
        void givenProperRequest_whenReadingMemberDetailProfile_thenReturnsMemberProfileInfo() throws Exception {
            // Given
            String mockMemberId = "mockMemberId";
            AdminMemberDetailReadResponseDto responseDto = AdminMemberDetailReadResponseDto.from(getMockUserMember());
            given(adminService.readMemberDetailProfile(any(AdminMemberDetailProfileRequestDto.class), any()))
                    .willReturn(responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/member/" + mockMemberId)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists())
                    .andExpect(jsonPath("$.result.memberId").exists());
        }
    }
}