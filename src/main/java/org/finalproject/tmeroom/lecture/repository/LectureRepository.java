package org.finalproject.tmeroom.lecture.repository;

import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, String> {
    @Query("select l.lectureCode from Lecture l")
    List<String> findAllLectureCode();

    Page<Lecture> findAllByLectureNameContaining(String lectureName, Pageable pageable);

    Page<Lecture> findAllByManagerIdContaining(String managerId, Pageable pageable);

    Page<Lecture> findAllByManagerId(String managerId, Pageable pageable);
}
