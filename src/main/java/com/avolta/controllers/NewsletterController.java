package com.avolta.controllers;

import com.avolta.dto.NewsletterSubscriberDto;
import com.avolta.dto.requests.NewsletterSubscriptionRequest;
import com.avolta.dto.responses.ApiResponse;
import com.avolta.services.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@Tag(name = "Newsletter", description = "Newsletter management API")
public class NewsletterController {

    private final NewsletterService newsletterService;

    @Operation(summary = "Get all newsletter subscribers", description = "Only authenticated users can access this endpoint")
    @GetMapping("/subscribers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NewsletterSubscriberDto>>> getAllSubscribers() {
        List<NewsletterSubscriberDto> subscribers = newsletterService.getAllSubscribers();
        return ResponseEntity.ok(ApiResponse.success(subscribers));
    }

    @Operation(summary = "Subscribe to newsletter", description = "Public endpoint")
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<NewsletterSubscriberDto>> subscribe(
            @Valid @RequestBody NewsletterSubscriptionRequest request) {
        NewsletterSubscriberDto subscriber = newsletterService.subscribe(request);
        return new ResponseEntity<>(ApiResponse.success("Subscribed successfully", subscriber), HttpStatus.CREATED);
    }

    @Operation(summary = "Unsubscribe from newsletter", description = "Public endpoint")
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(@RequestParam String email) {
        newsletterService.unsubscribe(email);
        return new ResponseEntity<>(ApiResponse.success("Unsubscribed successfully", null), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a subscriber", description = "Only authenticated users can access this endpoint")
    @DeleteMapping("/subscribers/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteSubscriber(@PathVariable String id) {
        newsletterService.deleteSubscriber(id);
        return new ResponseEntity<>(ApiResponse.success("Subscriber deleted successfully", null), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Send test email", description = "Only authenticated users can access this endpoint")
    @PostMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> sendTestEmail(@RequestParam String email) {
        newsletterService.sendTestEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Test email sent successfully", null));
    }
}