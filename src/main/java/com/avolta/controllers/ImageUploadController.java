// src/main/java/com/avolta/controllers/ImageUploadController.java
package com.avolta.controllers;

import com.avolta.dto.responses.ApiResponse;
import com.avolta.services.CloudinaryService;
import com.avolta.services.CloudinaryUploadResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image Upload", description = "API de gestion des images")
public class ImageUploadController {

    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Upload une image", description = "Upload une image vers Cloudinary")
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        
        try {
            log.info("Début de l'upload d'image: {}", file.getOriginalFilename());
            
            CloudinaryUploadResult result = cloudinaryService.uploadImage(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", result.getUrl());
            response.put("publicId", result.getPublicId());
            response.put("originalFileName", result.getOriginalFileName());
            response.put("fileSize", result.getFileSize());
            response.put("dimensions", Map.of(
                "width", result.getWidth(),
                "height", result.getHeight()
            ));
            
            log.info("Image uploadée avec succès: {}", result.getPublicId());
            
            return ResponseEntity.ok(
                ApiResponse.success("Image uploadée avec succès", response)
            );
            
        } catch (Exception e) {
            log.error("Erreur lors de l'upload d'image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Erreur lors de l'upload: " + e.getMessage()));
        }
    }

    @Operation(summary = "Supprime une image", description = "Supprime une image de Cloudinary")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable String publicId) {
        try {
            cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok(
                ApiResponse.success("Image supprimée avec succès", null)
            );
        } catch (Exception e) {
            log.error("Erreur lors de la suppression d'image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur lors de la suppression: " + e.getMessage()));
        }
    }
}