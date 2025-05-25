// src/main/java/com/avolta/services/CloudinaryService.java
package com.avolta.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload une image vers Cloudinary
     */
    public CloudinaryUploadResult uploadImage(MultipartFile file) throws IOException {
        try {
            // Validation du fichier
            validateImageFile(file);
            
            // Génération d'un nom unique
            String publicId = generateUniquePublicId();
            
            // Configuration de l'upload
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "avolta/publications", // Organiser par dossiers
                "resource_type", "image",
                "format", "auto", // Optimisation automatique du format
                "quality", "auto:good", // Optimisation automatique de la qualité
                "transformation", ObjectUtils.asMap(
                    "width", 1200,
                    "height", 800,
                    "crop", "limit", // Limite la taille sans déformer
                    "fetch_format", "auto" // Format optimal selon le navigateur
                )
            );

            // Upload vers Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(), 
                uploadParams
            );

            log.info("Image uploadée avec succès vers Cloudinary: {}", uploadResult.get("public_id"));

            return CloudinaryUploadResult.builder()
                .publicId((String) uploadResult.get("public_id"))
                .url((String) uploadResult.get("secure_url"))
                .originalFileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .format((String) uploadResult.get("format"))
                .width((Integer) uploadResult.get("width"))
                .height((Integer) uploadResult.get("height"))
                .build();

        } catch (Exception e) {
            log.error("Erreur lors de l'upload vers Cloudinary: {}", e.getMessage());
            throw new IOException("Échec de l'upload de l'image: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime une image de Cloudinary
     */
    public void deleteImage(String publicId) throws IOException {
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");
            
            if (!"ok".equals(resultStatus)) {
                log.warn("Image non trouvée ou déjà supprimée: {}", publicId);
            } else {
                log.info("Image supprimée avec succès: {}", publicId);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'image {}: {}", publicId, e.getMessage());
            throw new IOException("Échec de la suppression de l'image: " + e.getMessage(), e);
        }
    }

    /**
     * Génère une URL transformée pour une image
     */
    public String getTransformedImageUrl(String publicId, int width, int height, String cropMode) {
        return cloudinary.url()
            .transformation(ObjectUtils.asMap(
                "width", width,
                "height", height,
                "crop", cropMode,
                "quality", "auto",
                "fetch_format", "auto"
            ))
            .generate(publicId);
    }

    private void validateImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Le fichier doit être une image");
        }

        // Vérifier la taille (10MB max)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IOException("La taille du fichier ne doit pas dépasser 10MB");
        }

        // Vérifier les formats supportés
        String[] supportedFormats = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
        boolean isSupported = false;
        for (String format : supportedFormats) {
            if (format.equals(contentType)) {
                isSupported = true;
                break;
            }
        }
        
        if (!isSupported) {
            throw new IOException("Format d'image non supporté. Formats acceptés: JPEG, PNG, GIF, WebP");
        }
    }

    private String generateUniquePublicId() {
        return "avolta_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}