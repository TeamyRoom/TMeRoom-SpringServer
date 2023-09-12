package org.finalproject.tmeroom.question.repository;

import org.finalproject.tmeroom.question.data.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
