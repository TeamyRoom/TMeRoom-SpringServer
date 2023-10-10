package org.finalproject.TMeRoom.admin.service;

import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.admin.constant.LectureSearchType;
import org.finalproject.tmeroom.admin.constant.MemberRoleSearchType;
import org.finalproject.tmeroom.admin.constant.MemberSearchType;
import org.finalproject.tmeroom.admin.data.dto.request.*;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLectureDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLecturePageReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberPageReadResponseDto;
import org.finalproject.tmeroom.admin.service.AdminService;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.finalproject.TMeRoom.common.util.MockProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = AdminService.class)
@Import(value = MockProvider.class)
@DisplayName("어드민 관련 기능 테스트")
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private LectureRepository lectureRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private TeacherRepository teacherRepository;

    private static List<Lecture> getMockLecturesOfSize(int size, List<Member> mockManagers,
                                                       List<String> mockLectureNames) {
        return IntStream.range(0, size)
                .mapToObj(number -> getMockLecture(number, mockManagers, mockLectureNames))
                .toList();
    }

    private static Lecture getMockLecture(int number, List<Member> mockManagers, List<String> mockLectureNames) {
        return Lecture.builder()
                .lectureCode("mockLectureCode" + number)
                .lectureName(mockLectureNames.get(number % mockLectureNames.size()) + number)
                .manager(mockManagers.get(number % mockManagers.size()))
                .build();
    }

    private static List<Teacher> getMockTeachersOfSize(int size, List<Member> mockTeachers,
                                                       List<Lecture> mockLectures) {
        return IntStream.range(0, size)
                .mapToObj(number -> getMockTeacher(number, mockTeachers, mockLectures))
                .toList();
    }

    private static Teacher getMockTeacher(int number, List<Member> mockTeachers, List<Lecture> mockLectures) {
        return Teacher.builder()
                .member(mockTeachers.get(number % mockTeachers.size()))
                .lecture(mockLectures.get(number % mockLectures.size()))
                .build();
    }

    private static List<Student> getMockStudentsOfSize(int size, List<Member> mockStudents,
                                                       List<Lecture> mockLectures) {
        return IntStream.range(0, size)
                .mapToObj(number -> getMockStudent(number, mockStudents, mockLectures))
                .toList();
    }

    private static Student getMockStudent(int number, List<Member> mockStudents, List<Lecture> mockLectures) {
        return Student.builder()
                .member(mockStudents.get(number % mockStudents.size()))
                .lecture(mockLectures.get(number % mockLectures.size()))
                .build();
    }

    @Nested
    @DisplayName("회원 검색 기능 테스트")
    class aboutSearchingMembers {

        @Test
        @DisplayName("정상적인 요청이라면, 회원 검색시, 회원 검색 결과를 반환한다.")
        void givenProperRequest_whenSearchingMembers_thenReturnsMemberSearchPage() {

            // Given
            MemberSearchType searchType = MemberSearchType.ID;
            String keyword = "testerUser";
            Pageable mockPageable = mock(Pageable.class);
            AdminMemberSearchRequestDto requestDto = AdminMemberSearchRequestDto.builder()
                    .searchType(searchType)
                    .keyword(keyword)
                    .pageable(mockPageable)
                    .build();

            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            Member mockMember = getMockUserMember();
            List<Member> mockResultMembers = List.of(mockMember);
            Page<Member> mockPage = new PageImpl<>(mockResultMembers);

            given(memberRepository.findAllByIdContaining(eq(keyword), any(Pageable.class))).willReturn(mockPage);

            // When
            AdminMemberPageReadResponseDto responseDto = adminService.searchMembers(requestDto, mockAdminDto);

            // Then
            Page<AdminMemberDetailReadResponseDto> expected = mockPage.map(AdminMemberDetailReadResponseDto::from);
            Page<AdminMemberDetailReadResponseDto> actual = responseDto.getMembers();

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("강의 검색 기능 테스트")
    class aboutSearchingLectures {

        private List<Lecture> filterLecturesByName(List<Lecture> mockLectures, String keyword) {
            return mockLectures.stream()
                    .filter(lecture -> lecture.getLectureName().contains(keyword))
                    .toList();
        }

        private List<Lecture> filterLecturesByManager(List<Lecture> mockLectures, String keyword) {
            return mockLectures.stream()
                    .filter(lecture -> lecture.getManager().getId().contains(keyword))
                    .toList();
        }

        private void stubWithKeyword(List<Lecture> mockLectures,
                                     String keyword,
                                     BiFunction<List<Lecture>, String, List<Lecture>> lectureFilterByKeyword,
                                     BiFunction<String, Pageable, Page<Lecture>> query) {
            List<Lecture> filteredLectures = lectureFilterByKeyword.apply(mockLectures, keyword);
            given(query.apply(eq(keyword), any(Pageable.class))).willReturn(new PageImpl<>(filteredLectures));
        }

        void stubRepository(List<Lecture> mockLectures, String keyword, LectureSearchType searchType) {
            switch (searchType) {
                case NAME -> stubWithKeyword(mockLectures, keyword, this::filterLecturesByName,
                        lectureRepository::findAllByLectureNameContaining);
                case MANAGER -> stubWithKeyword(mockLectures, keyword, this::filterLecturesByManager,
                        lectureRepository::findAllByManagerIdContaining);
            }
        }

        private static Stream<Arguments> argProvider() {

            List<Member> MOCK_MANAGERS = List.of(
                    getMockManagerMember("TM"),
                    getMockManagerMember("JM"));
            List<String> MOCK_LECTURE_NAMES = List.of(
                    "Cloud Programmers",
                    "Cloud Engineers",
                    "AWS"
            );
            List<String> MOCK_KEYWORDS_LECTURE_NAME = List.of("Cloud", "Engineers", "k8s");
            List<String> MOCK_KEYWORDS_LECTURE_MANAGER_ID = List.of("TM", "JM", "randomGuy");
            int MOCK_SIZE = 10;

            List<Lecture> mockLectures = getMockLecturesOfSize(MOCK_SIZE, MOCK_MANAGERS, MOCK_LECTURE_NAMES);

            return Stream.of(
                    Arguments.of(mockLectures, MOCK_KEYWORDS_LECTURE_NAME.get(0), LectureSearchType.NAME),
                    Arguments.of(mockLectures, MOCK_KEYWORDS_LECTURE_NAME.get(1), LectureSearchType.NAME),
                    Arguments.of(mockLectures, MOCK_KEYWORDS_LECTURE_NAME.get(2), LectureSearchType.NAME),
                    Arguments.of(mockLectures, MOCK_KEYWORDS_LECTURE_MANAGER_ID.get(0), LectureSearchType.MANAGER),
                    Arguments.of(mockLectures, MOCK_KEYWORDS_LECTURE_MANAGER_ID.get(1), LectureSearchType.MANAGER),
                    Arguments.of(mockLectures, MOCK_KEYWORDS_LECTURE_MANAGER_ID.get(2), LectureSearchType.MANAGER)
            );
        }

        @ParameterizedTest
        @MethodSource("argProvider")
        @DisplayName("정상적인 요청이라면, 강의 검색시, 강의 검색 결과를 반환한다.")
        void givenProperRequest_whenSearchingLectures_thenReturnsLectureSearchPage(List<Lecture> mockLectures,
                                                                                   String keyword,
                                                                                   LectureSearchType searchType) {

            // Given
            stubRepository(mockLectures, keyword, searchType);

            Pageable mockPageable = mock(Pageable.class);
            AdminLectureSearchRequestDto requestDto = AdminLectureSearchRequestDto.builder()
                    .searchType(searchType)
                    .keyword(keyword)
                    .pageable(mockPageable)
                    .build();

            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            // When
            AdminLecturePageReadResponseDto responseDto = adminService.searchLectures(requestDto, mockAdminDto);

            // Then
            Page<AdminLectureDetailReadResponseDto> actual = responseDto.getLectures();
            Page<AdminLectureDetailReadResponseDto> expected = switch (searchType) {
                case NAME -> new PageImpl<>(mockLectures.stream()
                        .filter(lecture -> lecture.getLectureName().contains(keyword))
                        .map(AdminLectureDetailReadResponseDto::from)
                        .toList());
                case MANAGER -> new PageImpl<>(mockLectures.stream()
                        .filter(lecture -> lecture.getManager().getId().contains(keyword))
                        .map(AdminLectureDetailReadResponseDto::from)
                        .toList());
            };

            assertThat(actual).isEqualTo(expected);
            System.out.println(searchType + " 기준으로 " + keyword + " 검색한 결과: ");
            actual.getContent().forEach(System.out::println);
        }

        @Test
        @DisplayName("요청자가 관리자가 아니라면, 강의 검색시, 예외를 발생시킨다.")
        void givenNonAdminMember_whenSearchingLectures_thenThrowsException() {
            // Given
            LectureSearchType searchType = LectureSearchType.MANAGER;
            String mockKeyword = "mockKeyword";
            Pageable mockPageable = mock(Pageable.class);
            Page<Lecture> mockPage = mock(Page.class);
            AdminLectureSearchRequestDto requestDto = AdminLectureSearchRequestDto.builder()
                    .searchType(searchType)
                    .keyword(mockKeyword)
                    .pageable(mockPageable)
                    .build();
            MemberDto nonAdminMemberDto = MemberDto.from(getMockUserMember());
            given(lectureRepository.findAllByLectureNameContaining(mockKeyword, mockPageable)).willReturn(mockPage);

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.searchLectures(requestDto, nonAdminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.AUTHORIZATION_ERROR);
            then(lectureRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("회원 정보 조회 기능 테스트")
    class aboutReadingMemberDetailProfile {

        @Test
        @DisplayName("정상적인 요청이라면, 회원 정보 조회시, 회원 정보를 반환한다.")
        void givenProperRequest_whenReadingMemberDetailProfile_thenReturnsMemberProfile() {
            // Given
            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            Member mockUser = getMockUserMember();
            String mockUserId = mockUser.getId();
            AdminMemberDetailProfileRequestDto requestDto = new AdminMemberDetailProfileRequestDto();
            requestDto.setMemberId(mockUserId);
            given(memberRepository.findById(mockUserId)).willReturn(Optional.of(mockUser));

            // When
            AdminMemberDetailReadResponseDto responseDto =
                    adminService.readMemberDetailProfile(requestDto, mockAdminDto);

            // Then
            assertThat(responseDto)
                    .hasFieldOrPropertyWithValue("memberId", mockUser.getId())
                    .hasFieldOrPropertyWithValue("nickname", mockUser.getNickname())
                    .hasFieldOrPropertyWithValue("email", mockUser.getEmail());
        }

        @Test
        @DisplayName("요청자가 관리자가 아니라면, 회원 정보 조회시, 예외를 발생시킨다.")
        void givenNonAdminMember_whenReadingMemberDetailProfile_thenThrowsException() {
            // Given
            MemberDto nonAdminMemberDto = MemberDto.from(getMockUserMember());

            Member mockUser = getMockUserMember();
            String mockUserId = mockUser.getId();
            AdminMemberDetailProfileRequestDto requestDto = new AdminMemberDetailProfileRequestDto();
            requestDto.setMemberId(mockUserId);
            given(memberRepository.findById(mockUserId)).willReturn(Optional.of(mockUser));

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.readMemberDetailProfile(requestDto, nonAdminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.AUTHORIZATION_ERROR);
            then(lectureRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID라면, 회원 정보 조회시, 예외를 발생시킨다.")
        void givenInvalidMemberId_whenReadingMemberDetailProfile_thenThrowsException() {
            // Given
            MemberDto adminMemberDto = MemberDto.from(getMockAdminMember());

            String mockUserId = "invalidUserId";
            AdminMemberDetailProfileRequestDto requestDto = new AdminMemberDetailProfileRequestDto();
            requestDto.setMemberId(mockUserId);
            given(memberRepository.findById(mockUserId)).willReturn(Optional.empty());

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.readMemberDetailProfile(requestDto, adminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
            then(lectureRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("회원 관련 강의 정보 조회 테스트")
    class aboutReadingMemberDetailLecture {

        private List<Lecture> filterLecturesByStudent(List<?> mockStudents, String memberId) {
            return mockStudents.stream()
                    .map(object -> (Student) object)
                    .filter(student -> student.getStudentId().contains(memberId))
                    .map(Student::getLecture)
                    .toList();
        }

        private List<Lecture> filterLecturesByTeacher(List<?> mockTeacher, String memberId) {
            return mockTeacher.stream()
                    .map(object -> (Teacher) object)
                    .filter(teacher -> teacher.getTeacherId().contains(memberId))
                    .map(Teacher::getLecture)
                    .toList();
        }

        private List<Lecture> filterLecturesByManager(List<?> mockLectures, String memberId) {
            return mockLectures.stream()
                    .map(object -> (Lecture) object)
                    .filter(lecture -> lecture.getManager().getId().contains(memberId))
                    .toList();
        }

        private void stubWithMemberId(List<?> mockEntities,
                                      String memberId,
                                      BiFunction<List<?>, String, List<Lecture>> lectureFilterByKeyword,
                                      BiFunction<String, Pageable, Page<?>> query) {
            List<Lecture> filteredLectures = lectureFilterByKeyword.apply(mockEntities, memberId);

            Page mockPage = mock(Page.class);
            given(query.apply(eq(memberId), any(Pageable.class))).willReturn(mockPage);
            given(mockPage.map(any())).willReturn(new PageImpl<>(filteredLectures));
        }

        void stubRepository(List<?> mockEntities, String memberId, MemberRoleSearchType searchType) {
            Member mockMember = mock(Member.class);
            given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));
            given(mockMember.getId()).willReturn(memberId);

            switch (searchType) {
                case STUDENT -> stubWithMemberId(mockEntities, memberId, this::filterLecturesByStudent,
                        studentRepository::findAllByMemberId);
                case TEACHER -> stubWithMemberId(mockEntities, memberId, this::filterLecturesByTeacher,
                        teacherRepository::findAllByMemberId);
                case MANAGER -> stubWithMemberId(mockEntities, memberId, this::filterLecturesByManager,
                        lectureRepository::findAllByManagerId);
            }
        }

        private static Stream<Arguments> argProvider() {

            List<Member> MOCK_MANAGER_MEMBERS = List.of(
                    getMockManagerMember("TM"));
            List<Member> MOCK_TEACHER_MEMBERS = List.of(
                    getMockTeacherMember("JM"),
                    getMockTeacherMember("YS"));
            List<Member> MOCK_STUDENT_MEMBERS = List.of(
                    getMockStudentMember("JY"),
                    getMockStudentMember("JG"),
                    getMockStudentMember("MJ"));
            List<String> MOCK_LECTURE_NAMES = List.of(
                    "ClassA",
                    "ClassB",
                    "ClassC",
                    "ClassD",
                    "ClassE"
            );
            int MOCK_SIZE = 10;

            List<Lecture> mockLectures = getMockLecturesOfSize(MOCK_SIZE, MOCK_MANAGER_MEMBERS, MOCK_LECTURE_NAMES);
            List<Teacher> mockTeachers = getMockTeachersOfSize(MOCK_SIZE, MOCK_TEACHER_MEMBERS, mockLectures);
            List<Student> mockStudents = getMockStudentsOfSize(MOCK_SIZE, MOCK_STUDENT_MEMBERS, mockLectures);

            return Stream.of(
                    Arguments.of(mockLectures, mockLectures.get(0).getManager().getId(), MemberRoleSearchType.MANAGER),
                    Arguments.of(mockTeachers, mockTeachers.get(0).getTeacherId(), MemberRoleSearchType.TEACHER),
                    Arguments.of(mockTeachers, mockTeachers.get(1).getTeacherId(), MemberRoleSearchType.TEACHER),
                    Arguments.of(mockStudents, mockStudents.get(0).getStudentId(), MemberRoleSearchType.STUDENT),
                    Arguments.of(mockStudents, mockStudents.get(1).getStudentId(), MemberRoleSearchType.STUDENT)
            );
        }

        @ParameterizedTest
        @MethodSource("argProvider")
        @DisplayName("정상적인 요청이라면, 회원 관련 강의 조회시, 조회 결과를 반환한다.")
        void givenProperRequest_whenSearchingLectures_thenReturnsLectureSearchPage(List<?> mockEntities,
                                                                                   String memberId,
                                                                                   MemberRoleSearchType searchType) {

            // Given
            stubRepository(mockEntities, memberId, searchType);

            Pageable mockPageable = mock(Pageable.class);
            AdminMemberDetailLectureRequestDto requestDto = new AdminMemberDetailLectureRequestDto();
            requestDto.setSearchType(searchType);
            requestDto.setMemberId(memberId);
            requestDto.setPageable(mockPageable);

            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            // When
            AdminLecturePageReadResponseDto responseDto =
                    adminService.readMemberDetailLecture(requestDto, mockAdminDto);

            // Then
            Page<AdminLectureDetailReadResponseDto> actual = responseDto.getLectures();
            Page<AdminLectureDetailReadResponseDto> expected = switch (searchType) {
                case MANAGER -> new PageImpl<>(mockEntities.stream()
                        .map(entity -> (Lecture) entity)
                        .filter(lecture -> lecture.getManager().getId().contains(memberId))
                        .map(AdminLectureDetailReadResponseDto::from)
                        .toList());
                case STUDENT -> new PageImpl<>(mockEntities.stream()
                        .map(entity -> (Student) entity)
                        .filter(student -> student.getStudentId().contains(memberId))
                        .map(Student::getLecture)
                        .map(AdminLectureDetailReadResponseDto::from)
                        .toList());
                case TEACHER -> new PageImpl<>(mockEntities.stream()
                        .map(entity -> (Teacher) entity)
                        .filter(teacher -> teacher.getTeacherId().contains(memberId))
                        .map(Teacher::getLecture)
                        .map(AdminLectureDetailReadResponseDto::from)
                        .toList());
            };

            assertThat(actual).isEqualTo(expected);
            System.out.println(searchType + " 기준으로 " + memberId + " 조회한 결과: ");
            actual.getContent().forEach(System.out::println);
        }

        @Test
        @DisplayName("요청자가 관리자가 아니라면, 회원 정보 조회시, 예외를 발생시킨다.")
        void givenNonAdminMember_whenReadingMemberDetailLecture_thenThrowsException() {
            // Given
            MemberDto nonAdminMemberDto = MemberDto.from(getMockUserMember());

            String mockUserId = "mockUserId";
            MemberRoleSearchType mockSearchType = mock(MemberRoleSearchType.class);
            Pageable mockPageable = mock(Pageable.class);
            AdminMemberDetailLectureRequestDto requestDto = new AdminMemberDetailLectureRequestDto();
            requestDto.setMemberId(mockUserId);
            requestDto.setPageable(mockPageable);
            requestDto.setSearchType(mockSearchType);
            given(memberRepository.findById(mockUserId)).willReturn(Optional.empty());

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.readMemberDetailLecture(requestDto, nonAdminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.AUTHORIZATION_ERROR);
            then(lectureRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("강의 정보 조회 기능 테스트")
    class aboutReadingLectureInfo {

        private Lecture getMockLecture() {
            return Lecture.builder()
                    .lectureName("mockLectureName")
                    .lectureCode("mockLectureCode")
                    .manager(getMockManagerMember())
                    .build();
        }

        @Test
        @DisplayName("정상적인 요청이라면, 강의 정보 조회시, 회원 정보를 반환한다.")
        void givenProperRequest_whenReadingLectureInfo_thenReturnsLectureInfo() {
            // Given
            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            Lecture mockLecture = getMockLecture();
            AdminLectureDetailRequestDto requestDto = new AdminLectureDetailRequestDto();
            requestDto.setLectureCode(mockLecture.getLectureCode());
            given(lectureRepository.findById(requestDto.getLectureCode())).willReturn(Optional.of(mockLecture));

            // When
            AdminLectureDetailReadResponseDto responseDto =
                    adminService.readLectureInfo(requestDto, mockAdminDto);

            // Then
            assertThat(responseDto)
                    .hasFieldOrPropertyWithValue("lectureCode", mockLecture.getLectureCode())
                    .hasFieldOrPropertyWithValue("lectureName", mockLecture.getLectureName())
                    .hasFieldOrPropertyWithValue("managerId", mockLecture.getManager().getId());
        }

        @Test
        @DisplayName("요청자가 관리자가 아니라면, 강의 정보 조회시, 예외를 발생시킨다.")
        void givenNonAdminMember_whenReadingLectureInfo_thenThrowsException() {
            // Given
            MemberDto nonAdminMemberDto = MemberDto.from(getMockUserMember());

            Lecture mockLecture = getMockLecture();
            AdminLectureDetailRequestDto requestDto = new AdminLectureDetailRequestDto();
            requestDto.setLectureCode(mockLecture.getLectureCode());
            given(lectureRepository.findById(requestDto.getLectureCode())).willReturn(Optional.of(mockLecture));

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.readLectureInfo(requestDto, nonAdminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.AUTHORIZATION_ERROR);
        }

        @Test
        @DisplayName("존재하지 않는 강의 코드라면, 강의 정보 조회시, 예외를 발생시킨다.")
        void givenInvalidLectureCode_whenReadingLectureInfo_thenThrowsException() {
            // Given
            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            Lecture mockLecture = getMockLecture();
            AdminLectureDetailRequestDto requestDto = new AdminLectureDetailRequestDto();
            requestDto.setLectureCode(mockLecture.getLectureCode());
            given(lectureRepository.findById(any())).willReturn(Optional.empty());

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.readLectureInfo(requestDto, mockAdminDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.INVALID_LECTURE_CODE);
        }
    }

    @Nested
    @DisplayName("회원 삭제 기능 테스트")
    class aboutDeletingMember {

        @Test
        @DisplayName("정상적인 요청이라면, 회원 삭제시, 회원을 삭제한다.")
        void givenProperRequest_whenDeletingMember_thenDeletesMember() {
            // Given
            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            String memberId = "mockMemberId";

            // When
            adminService.deleteMember(memberId, mockAdminDto);

            // Then
            then(memberRepository).should().deleteById(memberId);
        }

        @Test
        @DisplayName("요청자가 관리자가 아니라면, 회원 삭제시, 예외를 발생시킨다.")
        void givenNonAdminMember_whenDeletingMember_thenThrowsException() {
            // Given
            MemberDto nonAdminMemberDto = MemberDto.from(getMockUserMember());

            String memberId = "mockMemberId";

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.deleteMember(memberId, nonAdminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.AUTHORIZATION_ERROR);
            then(memberRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("강의 삭제 기능 테스트")
    class aboutDeletingLecture {

        @Test
        @DisplayName("정상적인 요청이라면, 강의 삭제시, 강의를 삭제한다.")
        void givenProperRequest_whenDeletingLecture_thenDeletesLecture() {
            // Given
            MemberDto mockAdminDto = MemberDto.from(getMockAdminMember());

            String mockLectureCode = "mockLectureCode";

            // When
            adminService.deleteLecture(mockLectureCode, mockAdminDto);

            // Then
            then(lectureRepository).should().deleteById(mockLectureCode);
        }

        @Test
        @DisplayName("요청자가 관리자가 아니라면, 강의 삭제시, 예외를 발생시킨다.")
        void givenNonAdminMember_whenDeletingLecture_thenThrowsException() {
            // Given
            MemberDto nonAdminMemberDto = MemberDto.from(getMockUserMember());

            String mockLectureCode = "mockLectureCode";

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> adminService.deleteLecture(mockLectureCode, nonAdminMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.AUTHORIZATION_ERROR);
            then(memberRepository).shouldHaveNoInteractions();
        }
    }
}