package com.avolta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow all origins in development
        config.addAllowedOrigin("http://localhost:5173"); // Frontend Vite dev server
        config.addAllowedOrigin("http://localhost:4173"); // Frontend Vite preview
        // Ajouter l'origine de votre frontend déployé
        config.addAllowedOrigin("https://avoltabelgique.up.railway.app");
        // Allow all HTTP methods
        config.addAllowedMethod("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow credentials (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);
        
        // Expose the Authorization header
        config.addExposedHeader("Authorization");
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}