package com.avolta.dto;

import com.avolta.models.Publication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDto {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private LocalDateTime createdAt;
    private int likes;
    private String category;
    private Publication.Status status;
    private String authorId;
    private String authorName;
    private String authorEmail;
    private List<CommentDto> comments;

    public static PublicationDto fromEntity(Publication publication) {
        PublicationDto dto = new PublicationDto();
        dto.setId(publication.getId());
        dto.setTitle(publication.getTitle());
        dto.setContent(publication.getContent());
        dto.setImageUrl(publication.getImageUrl());
        dto.setValidFrom(publication.getValidFrom());
        dto.setValidTo(publication.getValidTo());
        dto.setCreatedAt(publication.getCreatedAt());
        dto.setLikes(publication.getLikes());
        dto.setCategory(publication.getCategory());
        dto.setStatus(publication.getStatus());
        dto.setAuthorId(publication.getAuthor().getId());
        dto.setAuthorName(publication.getAuthor().getEmail().split("@")[0]); // Simplified for example
        dto.setAuthorEmail(publication.getAuthor().getEmail());
        
        if (publication.getComments() != null) {
            dto.setComments(publication.getComments().stream()
                    .map(CommentDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}