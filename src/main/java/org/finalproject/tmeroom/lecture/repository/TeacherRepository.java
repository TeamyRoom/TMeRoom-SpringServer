package org.finalproject.tmeroom.lecture.repository;

import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.data.entity.TeacherPK;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, TeacherPK> {
    Teacher findByMemberIdAndLectureCode(String memberId, String lectureCode);
}
