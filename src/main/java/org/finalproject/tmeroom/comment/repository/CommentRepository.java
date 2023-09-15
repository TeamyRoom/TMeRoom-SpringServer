package org.finalproject.tmeroom.comment.repository;

import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
