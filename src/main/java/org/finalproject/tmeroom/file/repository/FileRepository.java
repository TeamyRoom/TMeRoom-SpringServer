package org.finalproject.tmeroom.file.repository;

import org.finalproject.tmeroom.file.data.entity.File;
import org.finalproject.tmeroom.file.data.entity.FileType;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    Page<File> findByLecture(Lecture lecture, Pageable pageable);

    Page<File> findAllByFileNameContainingAndLecture(String keyword, Lecture lecture, Pageable pageable);

    Page<File> findAllByFileNameContainingAndLectureAndFileType(String keyword, Lecture lecture, FileType fileType,
                                                                Pageable pageable);
}
