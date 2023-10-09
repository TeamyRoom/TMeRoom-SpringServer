package org.finalproject.tmeroom.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.file.data.dto.response.FileDetailResponseDto;
import org.finalproject.tmeroom.file.data.entity.File;
import org.finalproject.tmeroom.file.data.entity.FileType;
import org.finalproject.tmeroom.file.repository.FileRepository;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final FileRepository fileRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 조회 및 검색
    public Page<FileDetailResponseDto> findFilesByKeywordAndLecture(String keyword, String lectureCode,
                                                                    String fileType, Pageable pageable,
                                                                    MemberDto memberDto) {
        Lecture lecture = lectureRepository.getReferenceById(lectureCode);
        checkPermission(lecture, memberDto, AccessPermission.READ);
        Page<File> files;
        //TODO: Converter로 Refactoring하자
        try {
            FileType type = FileType.valueOf(fileType);
            files =
                    fileRepository.findAllByFileNameContainingAndLectureAndFileType(keyword, lecture, type, pageable);
        } catch (Exception e) {
            files =
                    fileRepository.findAllByFileNameContainingAndLecture(keyword, lecture, pageable);
        }

        return files.map(FileDetailResponseDto::from);
    }

    // 파일 등록
    public void registerFile(String lectureCode, MemberDto memberDto, List<MultipartFile> multipartFiles) {
        Lecture lecture = lectureRepository.getReferenceById(lectureCode);
        checkPermission(lecture, memberDto, AccessPermission.WRITE);

        uploadAndSaveFiles(multipartFiles, lecture, memberDto);
    }

    // 파일 s3 업로드
    private void uploadAndSaveFiles(List<MultipartFile> multipartFiles, Lecture lecture, MemberDto memberDto) {
        if (multipartFiles == null) {
            throw new ApplicationException(ErrorCode.NO_FILE_ERROR); //발생할 가능성 없어 보임
        }

        List<File> uploadedFiles = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                throw new ApplicationException(ErrorCode.EMPTY_FILE_ERROR); //이미지가 비어 있으면 예외 발생
            }
        }
        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String uuidFileName = getUuidFileName(originalFileName);
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            uploadFile(multipartFile, uuidFileName);

            File uploadedFile = File.builder()
                    .fileName(originalFileName)
                    .uuidFileName(uuidFileName)
                    .fileLink(amazonS3.getUrl(bucket, uuidFileName).toString())
                    .fileType(FileType.fromExtension(fileExtension))
                    .uploaderNickname(memberDto.getNickname())
                    .lecture(lecture)
                    .build();

            uploadedFiles.add(fileRepository.save(uploadedFile));
        }
    }

    private String getUuidFileName(String originalFileName) {
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID() + ext;
    }

    private void uploadFile(MultipartFile multipartFile, String uuidFileName) {
        try {
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(multipartFile.getInputStream().available());
            amazonS3.putObject(bucket, uuidFileName, multipartFile.getInputStream(), objMeta);
            log.info("uploaded file: url={}", amazonS3.getUrl(bucket, uuidFileName));
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.S3_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 파일 삭제
    public void deleteFile(String lectureCode, Long fileId, MemberDto memberDto) {
        File deleteFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_FILE_ID));

        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, memberDto, AccessPermission.DELETE);

        String originalFilename = deleteFile.getUuidFileName();

        amazonS3.deleteObject(bucket, originalFilename);
        fileRepository.delete(deleteFile);
    }

    private void checkPermission(Lecture lecture, MemberDto memberDto, AccessPermission requiredPermission) {
        if (memberDto == null) {
            throw new ApplicationException(ErrorCode.INVALID_ACCESS_PERMISSION);
        }

        Member member = memberRepository.getReferenceById(memberDto.getId());

        boolean hasPermission = false;

        switch (requiredPermission) {
            case READ -> hasPermission = studentRepository.existsByMemberAndLecture(member, lecture) ||
                    teacherRepository.existsByMemberAndLecture(member, lecture) ||
                    lecture.getManager().isIdMatch(memberDto.getId());
            case WRITE -> hasPermission = teacherRepository.existsByMemberAndLecture(member, lecture) ||
                    lecture.getManager().isIdMatch(memberDto.getId());
            case DELETE -> hasPermission = lecture.getManager().isIdMatch(memberDto.getId());
            default -> {
            }
        }

        if (hasPermission) {
            return;
        }

        throw new ApplicationException(ErrorCode.INVALID_ACCESS_PERMISSION);
    }

    public enum AccessPermission {
        READ,
        WRITE,
        DELETE
    }
}
