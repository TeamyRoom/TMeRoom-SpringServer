package org.finalproject.tmeroom.member.repository;

import org.finalproject.tmeroom.member.data.entity.Teacher;
import org.finalproject.tmeroom.member.data.entity.TeacherPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, TeacherPK> {
}
