package org.finalproject.TMeRoom.file.service;

import com.amazonaws.services.s3.AmazonS3;
import org.assertj.core.api.AssertionsForClassTypes;
import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.file.data.dto.response.FileDetailResponseDto;
import org.finalproject.tmeroom.file.data.entity.File;
import org.finalproject.tmeroom.file.data.entity.FileType;
import org.finalproject.tmeroom.file.repository.FileRepository;
import org.finalproject.tmeroom.file.service.FileService;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.finalproject.TMeRoom.common.util.MockProvider.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {FileService.class})
@Import(value = MockProvider.class)
@ActiveProfiles("test")
@DisplayName("파일 서비스")
class FileServiceTest {
    @Autowired
    private FileService fileService;

    @MockBean
    private FileRepository fileRepository;
    @MockBean
    private LectureRepository lectureRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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

    @Nested
    @DisplayName("파일 조회")
    class lookupFiles {
        @Test
        @DisplayName("파일 조회 요청시 파일 정보들이 불러와진다.")
        void success_return_files() {
            // Given
            String keyword = "test";
            Pageable mockPageable = mock(Pageable.class);
            String fileType = "CODE";

            Member studentMember = getMockStudentMember();
            MemberDto mockStudentDto = MemberDto.from(studentMember);
            File mockFile = getMockFile();
            List<File> mockResultFiles = List.of(mockFile);
            Page<File> mockPage = new PageImpl<>(mockResultFiles);

            given(fileRepository.findAllByFileNameContainingAndLectureAndFileType(eq(keyword),
                    eq(mockFile.getLecture()),
                    eq(FileType.CODE), any(Pageable.class))).willReturn(mockPage);
            given(lectureRepository.getReferenceById(mockFile.getLecture().getLectureCode())).willReturn(
                    mockFile.getLecture());
            given(studentRepository.existsByMemberAndLecture(studentMember, mockFile.getLecture())).willReturn(
                    true);
            given(memberRepository.getReferenceById(mockStudentDto.getId())).willReturn(studentMember);

            // When
            Page<FileDetailResponseDto> responseDto =
                    fileService.findFilesByKeywordAndLecture(keyword, mockFile.getLecture().getLectureCode(), fileType,
                            mockPageable, mockStudentDto);

            // Then
            Page<FileDetailResponseDto> expected = mockPage.map(FileDetailResponseDto::from);
            Page<FileDetailResponseDto> actual = responseDto;

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("권한이 없는 사람이 조회 요청시 예외가 발생한다.")
        void fail_return_exception() {
            // Given
            String keyword = "test";
            Pageable mockPageable = mock(Pageable.class);
            String fileType = "CODE";

            MemberDto mockAnonymousDto = MemberDto.from(getMockAnonymousMember());
            File mockFile = getMockFile();
            List<File> mockResultFiles = List.of(mockFile);
            Page<File> mockPage = new PageImpl<>(mockResultFiles);

            given(fileRepository.findAllByFileNameContainingAndLectureAndFileType(eq(keyword),
                    eq(mockFile.getLecture()),
                    eq(FileType.CODE), any(Pageable.class))).willReturn(mockPage);
            given(lectureRepository.getReferenceById(mockFile.getLecture().getLectureCode())).willReturn(
                    mockFile.getLecture());

            // When
            Throwable throwable = catchThrowable(
                    () -> fileService.findFilesByKeywordAndLecture(keyword, mockFile.getLecture().getLectureCode(),
                            fileType, mockPageable, mockAnonymousDto));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_ACCESS_PERMISSION.getMessage());
        }
    }


    @Nested
    @DisplayName("파일 등록")
    class registerFile {
        @Test
        @DisplayName("파일 등록 요청시 db에 파일 정보가 저장 된다.")
        void registerFile_then_registerFile() {
            //given
            //가짜 멀티파트 이미지 5장 생성
            List<MultipartFile> imageMultipartFiles = IntStream.range(0, 1).boxed()
                    .map(i -> (MultipartFile) new MockMultipartFile("image", "original_file_name_" + i + ".jpg",
                            "image/jpeg", new byte[10]))
                    .collect(Collectors.toList());

            Member mockTeacher = getMockTeacherMember();
            MemberDto mockTeacherDto = MemberDto.from(mockTeacher);
            Lecture lecture = getMockLecture("강의명", getMockManagerMember());

            given(lectureRepository.getReferenceById(lecture.getLectureCode())).willReturn(
                    lecture);
            given(teacherRepository.existsByMemberAndLecture(mockTeacher, lecture)).willReturn(true);
            given(memberRepository.getReferenceById(mockTeacher.getId())).willReturn(mockTeacher);
            given(amazonS3.putObject(any(), any(), any(), any())).willReturn(null);
            given(amazonS3.getUrl(any(), any())).willReturn(mock(URL.class));

            //when
            fileService.registerFile(MOCK_LECTURE_CODE, mockTeacherDto, imageMultipartFiles);

            //then
            then(fileRepository).should().save(any(File.class));
        }

        @Test
        @DisplayName("비어_있는_이미지를_업로드하면_예외_발생")
        public void registerEmptyImage_then_returnException () {

            //given
            MockMultipartFile emptyImage = new MockMultipartFile("image", "original_file_name.jpg",
                    "image/jpeg", new byte[0]);
            List<MultipartFile> imageMultipartFiles = Arrays.asList(emptyImage);

            Lecture lecture = getMockLecture("강의명", getMockManagerMember());

            Member mockTeacher = getMockTeacherMember();
            MemberDto mockTeacherDto = MemberDto.from(mockTeacher);
            given(lectureRepository.getReferenceById(lecture.getLectureCode())).willReturn(
                    lecture);
            given(teacherRepository.existsByMemberAndLecture(mockTeacher, lecture)).willReturn(true);
            given(memberRepository.getReferenceById(mockTeacher.getId())).willReturn(mockTeacher);

            //when
            ApplicationException ex = assertThrows(ApplicationException.class, () -> fileService.registerFile(MOCK_LECTURE_CODE, mockTeacherDto, imageMultipartFiles));

            //then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_FILE_ERROR);
        }

        @Test
        @DisplayName("권한이 없는 사용자가 파일 등록 요청시 예외를 반환 한다.")
        void fail_then_returnException() {
            //given
            //가짜 멀티파트 이미지 5장 생성
            List<MultipartFile> imageMultipartFiles = IntStream.range(0, 1).boxed()
                    .map(i -> (MultipartFile) new MockMultipartFile("image", "original_file_name_" + i + ".jpg",
                            "image/jpeg", new byte[10]))
                    .collect(Collectors.toList());

            Member mockAnonymous = getMockAnonymousMember();
            MemberDto mockAnonymousDto = MemberDto.from(mockAnonymous);
            Lecture lecture = getMockLecture("강의명", getMockManagerMember());

            given(lectureRepository.getReferenceById(lecture.getLectureCode())).willReturn(
                    lecture);

            //when
            Throwable throwable = catchThrowable(
                    () -> fileService.registerFile(MOCK_LECTURE_CODE, mockAnonymousDto, imageMultipartFiles));

            //then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_ACCESS_PERMISSION.getMessage());
        }
    }

    @Nested
    @DisplayName("파일 삭제")
    class deleteFile {
        @Test
        @DisplayName("파일 삭제 요청시 파일이 s3와 레포지토리에서 삭제된다.")
        void deleteFile_then_deleteFile() {
            // Given
            File mockFile = getMockFile();
            Member mockManager = getMockManagerMember();
            MemberDto mockManagerDto = MemberDto.from(mockManager);
            Lecture lecture = getMockLecture(MOCK_LECTURE_CODE, mockManager);

            given(fileRepository.findById(mockFile.getId())).willReturn(Optional.of(mockFile));
            given(memberRepository.getReferenceById(mockManager.getId())).willReturn(mockManager);
            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));

            // When
            fileService.deleteFile(MOCK_LECTURE_CODE, mockFile.getId(), mockManagerDto);

            // Then
            then(fileRepository).should().delete(any(File.class));
        }

        @Test
        @DisplayName("권한이 없는 사용자가 파일 삭제 요청시 예외가 반환 된다.")
        void NoPermissionUser_DeleteFile_ReturnException() {
            // Given
            File mockFile = getMockFile();
            Member mockAnonymous = getMockAnonymousMember();
            MemberDto mockAnonymousDto = MemberDto.from(mockAnonymous);
            Lecture lecture = getMockLecture(MOCK_LECTURE_CODE, getMockManagerMember());

            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(fileRepository.findById(mockFile.getId())).willReturn(Optional.of(mockFile));
            given(memberRepository.getReferenceById(mockAnonymousDto.getId())).willReturn(mockAnonymous);

            // When
            Throwable throwable = catchThrowable(
                    () -> fileService.deleteFile(MOCK_LECTURE_CODE, mockFile.getId(), mockAnonymousDto));

            //then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_ACCESS_PERMISSION.getMessage());
        }
    }
}