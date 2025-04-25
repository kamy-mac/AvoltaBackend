package com.avolta.dto;

import com.avolta.models.NewsletterSubscriber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscriberDto {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private boolean confirmed;
    private LocalDateTime lastSentAt;

    public static NewsletterSubscriberDto fromEntity(NewsletterSubscriber subscriber) {
        NewsletterSubscriberDto dto = new NewsletterSubscriberDto();
        dto.setId(subscriber.getId());
        dto.setEmail(subscriber.getEmail());
        dto.setFirstName(subscriber.getFirstName());
        dto.setLastName(subscriber.getLastName());
        dto.setCreatedAt(subscriber.getCreatedAt());
        dto.setConfirmed(subscriber.isConfirmed());
        dto.setLastSentAt(subscriber.getLastSentAt());
        return dto;
    }
}