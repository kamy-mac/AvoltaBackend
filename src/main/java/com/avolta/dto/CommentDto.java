package com.avolta.dto;

import com.avolta.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor().getEmail().split("@")[0]); // Simplified for example
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}