package com.avolta.repositories;

import com.avolta.models.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, String> {
    Optional<NewsletterSubscriber> findByEmail(String email);
    boolean existsByEmail(String email);
}