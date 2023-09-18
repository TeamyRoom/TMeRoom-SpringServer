package org.finalproject.TMeRoom.lecture.service;

import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureCreateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureUpdateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.response.LectureCreateResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.service.LectureService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {LectureService.class})
@ActiveProfiles("test")
@DisplayName("강의 서비스")
public class LectureServiceTest {
    @Autowired
    private LectureService lectureService;
    @MockBean
    private LectureRepository lectureRepository;
    @MockBean
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("강의 기능 테스트")
    class aboutLecture {
        @Test
        @DisplayName("강의 관리자가 강의명을 넘겨주면 강의 생성 후 코드를 반환한다.")
        void givenCreateRequest_whenCreateLecture_thenReturnLectureCode() {
            //Given
            given(memberRepository.findById("manager")).willReturn(Optional.of(Member.builder().id("manager").build()));
            LectureCreateRequestDto requestDTO = new LectureCreateRequestDto();
            requestDTO.setLectureName("Hello! World");
            requestDTO.setMemberDTO(new MemberDto("manager"));

            //When
            LectureCreateResponseDto responseDTO = lectureService.createLecture(requestDTO);

            //Then
            assertThat(responseDTO.getLectureCode()).isNotEmpty();
        }

        @Test
        @DisplayName("강의 관리자가 강의 삭제를 요청하면 강의가 삭제된다.")
        void givenDeleteRequest_whenDeleteLecture_thenReturnLectureCode() {
            //Given
            Lecture mockLecture = mock(Lecture.class);
            MemberDto manager = mock(MemberDto.class);
            given(manager.getId()).willReturn("manager");
            given(mockLecture.getManager()).willReturn(Member.builder().id("manager").build());
            given(lectureRepository.getReferenceById("1234")).willReturn(mockLecture);

            //When
            lectureService.deleteLecture("1234", manager);

            //Then
            then(lectureRepository).should().delete(mockLecture);
        }

        @Test
        @DisplayName("강의 관리자가 아닌 사람이 강의 삭제를 요청하면 예외가 발생한다.")
        void givenNoOwnerDeleteRequest_whenDeleteLecture_thenOccurredException() {
            //Given
            Lecture mockLecture = mock(Lecture.class);
            MemberDto noManager = mock(MemberDto.class);
            given(noManager.getId()).willReturn("noManager");
            given(mockLecture.getManager()).willReturn(Member.builder().id("manager").build());
            given(lectureRepository.getReferenceById("1234")).willReturn(mockLecture);

            //When
            Throwable throwable = catchThrowable(() -> lectureService.deleteLecture("1234", noManager));

            //Then
            assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }

        @Test
        @DisplayName("강의 관리자가 강의명 변경을 요청하면 강의명이 변경된다.")
        void givenUpdateRequest_whenUpdateLecture_thenReturnLectureName() {
            //Given
            MemberDto managerDto = mock(MemberDto.class);
            Member manager = mock(Member.class);
            Lecture mockLecture = getMockLecture("1234", manager);
            given(manager.getId()).willReturn("manager");
            given(managerDto.getId()).willReturn("manager");
            given(lectureRepository.findById("1234")).willReturn(Optional.of(mockLecture));
            given(memberRepository.findById("manager")).willReturn(Optional.of(manager));

            LectureUpdateRequestDto requestDTO = new LectureUpdateRequestDto();
            String modifiedName = "Hello! World2";
            requestDTO.setLectureName(modifiedName);
            requestDTO.setMemberDTO(managerDto);
            requestDTO.setLectureCode("1234");

            //When
            lectureService.updateLecture(requestDTO);

            //Then
            assertThat(mockLecture.getLectureName()).isEqualTo(modifiedName);
        }

        private Member getMockManager() {
            return Member.builder()
                    .id("manager")
                    .build();
        }

        private Lecture getMockLecture(String 강의명, Member 관리자) {
            return Lecture.builder()
                    .lectureCode("1234")
                    .lectureName(강의명)
                    .manager(관리자)
                    .build();
        }
    }
}