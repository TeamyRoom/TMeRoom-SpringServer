package org.finalproject.tmeroom.lecture.repository;

import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.StudentPK;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, StudentPK> {
    Student findByMemberIdAndLectureCode(String memberId, String lectureCode);

    Page<Student> findByLecture(Pageable pageable, Lecture lecture);

    Page<Student> findByMember(Pageable pageable, Member member);
}
