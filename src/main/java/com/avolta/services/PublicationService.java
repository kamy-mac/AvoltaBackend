package com.avolta.services;

import com.avolta.dto.PublicationDto;
import com.avolta.dto.requests.CreatePublicationRequest;
import com.avolta.dto.requests.UpdatePublicationRequest;
import com.avolta.exceptions.ResourceNotFoundException;
import com.avolta.models.Publication;
import com.avolta.models.User;
import com.avolta.repositories.PublicationRepository;
import com.avolta.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final NewsletterService newsletterService;
    @Value("${app.api-base-url}")
    private String apiBaseUrl;

    @Transactional(readOnly = true)
    public List<PublicationDto> getAllPublications() {
        return publicationRepository.findAll().stream()
                .map(PublicationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PublicationDto> getActivePublications() {
        return publicationRepository.findActivePublications(LocalDateTime.now()).stream()
                .map(PublicationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PublicationDto> getActivePublicationsByCategory(String category) {
        return publicationRepository.findActivePublicationsByCategory(LocalDateTime.now(), category).stream()
                .map(PublicationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PublicationDto> getPendingPublications() {
        return publicationRepository.findByStatus(Publication.Status.PENDING).stream()
                .map(PublicationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PublicationDto getPublicationById(String id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + id));
        return PublicationDto.fromEntity(publication);
    }

    @Transactional
    public PublicationDto createPublication(CreatePublicationRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + authorEmail));

        Publication publication = new Publication();
        publication.setTitle(request.getTitle());
        publication.setContent(request.getContent());
        publication.setImageUrl(request.getImageUrl());
        publication.setValidFrom(request.getValidFrom());
        publication.setValidTo(request.getValidTo());
        publication.setCategory(request.getCategory());
        publication.setAuthor(author);

        // If author is SUPERADMIN, publish immediately, otherwise set as PENDING
        if (author.getRole() == User.Role.SUPERADMIN) {
            publication.setStatus(Publication.Status.PUBLISHED);
        } else {
            publication.setStatus(Publication.Status.PENDING);
        }

        Publication savedPublication = publicationRepository.save(publication);

        // Si l'URL de l'image est relative, la convertir en URL absolue pour le
        // frontend
        String imageUrl = request.getImageUrl();
        // if (imageUrl != null && imageUrl.startsWith("/api/uploads/")) {
        //     // Convertir l'URL relative en URL absolue
        //     if (!imageUrl.startsWith("http")) {
        //         publication.setImageUrl(apiBaseUrl + imageUrl);
        //         System.out.println("URL de l'image convertie en absolu: " + publication.getImageUrl());
        //     } else {
        //         publication.setImageUrl(imageUrl);
        //     }
        // } else
        if (imageUrl != null && imageUrl.startsWith("/api/")) {
            // Convertir l'URL relative en URL absolue
            if (!imageUrl.startsWith("http")) {
                publication.setImageUrl(apiBaseUrl + imageUrl);
                System.out.println("URL de l'image convertie en absolu: " + publication.getImageUrl());
            } else {
                publication.setImageUrl(imageUrl);
            }
        } else {
            publication.setImageUrl(imageUrl);
        }

        // If publication is published and sendNewsletter is true, send newsletter
        if (publication.getStatus() == Publication.Status.PUBLISHED && request.isSendNewsletter()) {
            newsletterService.sendNewsletter(savedPublication);
        }

        return PublicationDto.fromEntity(savedPublication);
    }

    @Transactional
    public PublicationDto updatePublication(String id, UpdatePublicationRequest request) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + id));

        if (request.getTitle() != null) {
            publication.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            publication.setContent(request.getContent());
        }
        if (request.getImageUrl() != null) {
            publication.setImageUrl(request.getImageUrl());
        }
        if (request.getValidFrom() != null) {
            publication.setValidFrom(request.getValidFrom());
        }
        if (request.getValidTo() != null) {
            publication.setValidTo(request.getValidTo());
        }
        if (request.getCategory() != null) {
            publication.setCategory(request.getCategory());
        }

        Publication updatedPublication = publicationRepository.save(publication);
        return PublicationDto.fromEntity(updatedPublication);
    }

    @Transactional
    public PublicationDto approvePublication(String id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + id));

        publication.setStatus(Publication.Status.PUBLISHED);
        Publication approvedPublication = publicationRepository.save(publication);
        return PublicationDto.fromEntity(approvedPublication);
    }

    @Transactional
    public void rejectPublication(String id) {
        if (!publicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Publication not found with id: " + id);
        }
        publicationRepository.deleteById(id);
    }

    @Transactional
    public void deletePublication(String id) {
        if (!publicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Publication not found with id: " + id);
        }
        publicationRepository.deleteById(id);
    }

    @Transactional
    public PublicationDto likePublication(String id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found with id: " + id));

        publication.setLikes(publication.getLikes() + 1);
        Publication updatedPublication = publicationRepository.save(publication);
        return PublicationDto.fromEntity(updatedPublication);
    }
}