package org.finalproject.tmeroom.member.repository;

import org.finalproject.tmeroom.member.data.entity.Student;
import org.finalproject.tmeroom.member.data.entity.StudentPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, StudentPK> {
}
