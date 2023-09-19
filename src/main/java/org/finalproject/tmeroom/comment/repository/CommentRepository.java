package org.finalproject.tmeroom.comment.repository;

import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByQuestion(Pageable pageable, Question question);
}
