package com.avolta.services;

import com.avolta.dto.NewsletterSubscriberDto;
import com.avolta.dto.requests.NewsletterSubscriptionRequest;
import com.avolta.exceptions.ResourceNotFoundException;
import com.avolta.models.NewsletterSubscriber;
import com.avolta.models.Publication;
import com.avolta.repositories.NewsletterSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;
    // In a real application, you would inject an email service here
    // private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<NewsletterSubscriberDto> getAllSubscribers() {
        return subscriberRepository.findAll().stream()
                .map(NewsletterSubscriberDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public NewsletterSubscriberDto subscribe(NewsletterSubscriptionRequest request) {
        // Check if subscriber already exists
        if (subscriberRepository.existsByEmail(request.getEmail())) {
            NewsletterSubscriber existingSubscriber = subscriberRepository.findByEmail(request.getEmail())
                    .orElseThrow(); // This should never happen due to the existsByEmail check
            
            // Update fields if provided
            if (request.getFirstName() != null) {
                existingSubscriber.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                existingSubscriber.setLastName(request.getLastName());
            }
            
            // Set as confirmed (in a real app, you would send a confirmation email)
            existingSubscriber.setConfirmed(true);
            
            NewsletterSubscriber updatedSubscriber = subscriberRepository.save(existingSubscriber);
            return NewsletterSubscriberDto.fromEntity(updatedSubscriber);
        }
        
        // Create new subscriber
        NewsletterSubscriber subscriber = new NewsletterSubscriber();
        subscriber.setEmail(request.getEmail());
        subscriber.setFirstName(request.getFirstName());
        subscriber.setLastName(request.getLastName());
        subscriber.setConfirmed(true); // In a real app, this would be false until confirmed via email
        
        NewsletterSubscriber savedSubscriber = subscriberRepository.save(subscriber);
        
        // In a real app, you would send a confirmation email here
        // emailService.sendConfirmationEmail(savedSubscriber);
        
        return NewsletterSubscriberDto.fromEntity(savedSubscriber);
    }

    @Transactional
    public void unsubscribe(String email) {
        NewsletterSubscriber subscriber = subscriberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with email: " + email));
        
        subscriberRepository.delete(subscriber);
    }

    @Transactional
    public void deleteSubscriber(String id) {
        if (!subscriberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscriber not found with id: " + id);
        }
        subscriberRepository.deleteById(id);
    }

    @Transactional
    public void sendNewsletter(Publication publication) {
        List<NewsletterSubscriber> confirmedSubscribers = subscriberRepository.findAll().stream()
                .filter(NewsletterSubscriber::isConfirmed)
                .collect(Collectors.toList());
        
        // In a real app, you would send emails to all confirmed subscribers
        // emailService.sendNewsletterAboutPublication(confirmedSubscribers, publication);
        
        // Update last_sent_at for all subscribers
        LocalDateTime now = LocalDateTime.now();
        confirmedSubscribers.forEach(subscriber -> {
            subscriber.setLastSentAt(now);
            subscriberRepository.save(subscriber);
        });
        
        // Log the newsletter sending
        System.out.println("Newsletter about publication '" + publication.getTitle() + 
                "' sent to " + confirmedSubscribers.size() + " subscribers");
    }

    @Transactional
    public void sendTestEmail(String email) {
        // In a real app, you would send a test email
        // emailService.sendTestEmail(email);
        
        System.out.println("Test email sent to: " + email);
    }
}