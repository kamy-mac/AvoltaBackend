package com.avolta.controllers;

import com.avolta.dto.PublicationDto;
import com.avolta.dto.requests.CreatePublicationRequest;
import com.avolta.dto.requests.UpdatePublicationRequest;
import com.avolta.dto.responses.ApiResponse;
import com.avolta.services.PublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
@RequiredArgsConstructor
@Tag(name = "Publications", description = "Publication management API")
public class PublicationController {

    private final PublicationService publicationService;

    @Operation(summary = "Get all publications", description = "Requires authentication")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PublicationDto>>> getAllPublications() {
        List<PublicationDto> publications = publicationService.getAllPublications();
        return ResponseEntity.ok(ApiResponse.success(publications));
    }

    @Operation(summary = "Get active publications", description = "Public endpoint")
    @GetMapping("/public/active")
    public ResponseEntity<ApiResponse<List<PublicationDto>>> getActivePublications() {
        List<PublicationDto> publications = publicationService.getActivePublications();
        return ResponseEntity.ok(ApiResponse.success(publications));
    }

    @Operation(summary = "Get active publications by category", description = "Public endpoint")
    @GetMapping("/public/category/{category}")
    public ResponseEntity<ApiResponse<List<PublicationDto>>> getActivePublicationsByCategory(
            @PathVariable String category) {
        List<PublicationDto> publications = publicationService.getActivePublicationsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(publications));
    }

    @Operation(summary = "Get pending publications", description = "Only super admins can access this endpoint")
    // Dans PublicationController.java
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<ApiResponse<List<PublicationDto>>> getPendingPublications() {
        List<PublicationDto> publications = publicationService.getPendingPublications();
        return ResponseEntity.ok(ApiResponse.success(publications));
    }

    // @Operation(summary = "Get publication by ID", description = "Public endpoint for published publications")
    // @GetMapping("/public/{id}")
    // public ResponseEntity<ApiResponse<PublicationDto>> getPublicationById(@PathVariable String id) {
    //     PublicationDto publication = publicationService.getPublicationById(id);
    //     return ResponseEntity.ok(ApiResponse.success(publication));
    // }

    @Operation(summary = "Get publication by ID", description = "Public endpoint for all publications")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicationDto>> getPublicationById(@PathVariable String id) {
        PublicationDto publication = publicationService.getPublicationById(id);
        return ResponseEntity.ok(ApiResponse.success(publication));
    }

    @Operation(summary = "Get publication by ID", description = "Public endpoint for published publications (legacy endpoint)")
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<PublicationDto>> getPublicPublicationById(@PathVariable String id) {
        PublicationDto publication = publicationService.getPublicationById(id);
        return ResponseEntity.ok(ApiResponse.success(publication));
    }



    @Operation(summary = "Create a new publication", description = "Requires authentication")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PublicationDto>> createPublication(
            @Valid @RequestBody CreatePublicationRequest request,
            Authentication authentication) {
        PublicationDto createdPublication = publicationService.createPublication(request, authentication.getName());
        return new ResponseEntity<>(ApiResponse.success("Publication created successfully", createdPublication),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update a publication", description = "Requires authentication")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PublicationDto>> updatePublication(
            @PathVariable String id,
            @RequestBody UpdatePublicationRequest request) {
        PublicationDto updatedPublication = publicationService.updatePublication(id, request);
        return ResponseEntity.ok(ApiResponse.success("Publication updated successfully", updatedPublication));
    }

    @Operation(summary = "Approve a publication", description = "Only super admins can access this endpoint")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<PublicationDto>> approvePublication(@PathVariable String id) {
        PublicationDto approvedPublication = publicationService.approvePublication(id);
        return ResponseEntity.ok(ApiResponse.success("Publication approved successfully", approvedPublication));
    }

    @Operation(summary = "Reject a publication", description = "Only super admins can access this endpoint")
    @DeleteMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectPublication(@PathVariable String id) {
        publicationService.rejectPublication(id);
        return new ResponseEntity<>(ApiResponse.success("Publication rejected successfully", null),
                HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a publication", description = "Requires authentication")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deletePublication(@PathVariable String id) {
        publicationService.deletePublication(id);
        return new ResponseEntity<>(ApiResponse.success("Publication deleted successfully", null),
                HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Like a publication", description = "Public endpoint")
    @PostMapping("/public/{id}/like")
    public ResponseEntity<ApiResponse<PublicationDto>> likePublication(@PathVariable String id) {
        PublicationDto likedPublication = publicationService.likePublication(id);
        return ResponseEntity.ok(ApiResponse.success("Publication liked successfully", likedPublication));
    }
}