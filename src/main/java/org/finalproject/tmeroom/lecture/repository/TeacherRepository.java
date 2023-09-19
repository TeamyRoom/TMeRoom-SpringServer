package org.finalproject.tmeroom.lecture.repository;

import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.data.entity.TeacherPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, TeacherPK> {
    Teacher findByMemberIdAndLectureCode(String memberId, String lectureCode);

    Page<Teacher> findByLecture(Pageable pageable, Lecture lecture);
}
