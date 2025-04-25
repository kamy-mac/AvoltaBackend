package com.avolta.repositories;

import com.avolta.models.Publication;
import com.avolta.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, String> {
    List<Publication> findByAuthor(User author);
    List<Publication> findByStatus(Publication.Status status);
    
    @Query("SELECT p FROM Publication p WHERE p.status = 'PUBLISHED' AND p.validFrom <= :now AND p.validTo >= :now")
    List<Publication> findActivePublications(LocalDateTime now);
    
    @Query("SELECT p FROM Publication p WHERE p.status = 'PUBLISHED' AND p.validFrom <= :now AND p.validTo >= :now AND p.category = :category")
    List<Publication> findActivePublicationsByCategory(LocalDateTime now, String category);
}