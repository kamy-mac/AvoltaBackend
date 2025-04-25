package com.avolta.dto.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePublicationRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String imageUrl;
    
    @NotNull(message = "Valid from date is required")
    private LocalDateTime validFrom;
    
    @NotNull(message = "Valid to date is required")
    @Future(message = "Valid to date must be in the future")
    private LocalDateTime validTo;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private boolean sendNewsletter = false;
}