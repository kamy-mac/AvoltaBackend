package com.avolta.repositories;

import com.avolta.models.Comment;
import com.avolta.models.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPublicationOrderByCreatedAtDesc(Publication publication);
}