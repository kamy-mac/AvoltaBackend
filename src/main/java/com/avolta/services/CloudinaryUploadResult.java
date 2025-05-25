// src/main/java/com/avolta/services/CloudinaryUploadResult.java
package com.avolta.services;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloudinaryUploadResult {
    private String publicId;
    private String url;
    private String originalFileName;
    private Long fileSize;
    private String format;
    private Integer width;
    private Integer height;
}