package com.avolta.services;

import com.avolta.dto.CommentDto;
import com.avolta.dto.requests.CreateCommentRequest;
import com.avolta.exceptions.ResourceNotFoundException;
import com.avolta.models.Comment;
import com.avolta.models.Publication;
import com.avolta.models.User;
import com.avolta.repositories.CommentRepository;
import com.avolta.repositories.PublicationRepository;
import com.avolta.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPublicationId(String publicationId) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + publicationId));
        
        return commentRepository.findByPublicationOrderByCreatedAtDesc(publication).stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(String publicationId, CreateCommentRequest request, String authorEmail) {
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + publicationId));
        
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + authorEmail));
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPublication(publication);
        comment.setAuthor(author);
        
        Comment savedComment = commentRepository.save(comment);
        return CommentDto.fromEntity(savedComment);
    }

    @Transactional
    public void deleteComment(String id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}