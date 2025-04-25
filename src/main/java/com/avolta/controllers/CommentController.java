package com.avolta.controllers;

import com.avolta.dto.CommentDto;
import com.avolta.dto.requests.CreateCommentRequest;
import com.avolta.dto.responses.ApiResponse;
import com.avolta.services.CommentService;
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
@RequestMapping("/api/publications/{publicationId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment management API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Get all comments for a publication", description = "Public endpoint")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDto>>> getCommentsByPublicationId(@PathVariable String publicationId) {
        List<CommentDto> comments = commentService.getCommentsByPublicationId(publicationId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @Operation(summary = "Create a new comment", description = "Requires authentication")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @PathVariable String publicationId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        CommentDto createdComment = commentService.createComment(publicationId, request, authentication.getName());
        return new ResponseEntity<>(ApiResponse.success("Comment created successfully", createdComment), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a comment", description = "Requires authentication")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(ApiResponse.success("Comment deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}