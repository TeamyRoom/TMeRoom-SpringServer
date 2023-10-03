package org.finalproject.tmeroom.file.repository;

import org.finalproject.tmeroom.file.data.entity.File;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, Long> {
    Page<File> findByLecture(Lecture lecture, Pageable pageable);

    @Query(value = "SELECT f from File f "+
    "WHERE f.lecture =:lecture AND f.fileName like %:keyword%")
    Page<File> findFilesByKeywordAndLecture(@Param("keyword")String keyword, Lecture lecture, Pageable pageable);
}
