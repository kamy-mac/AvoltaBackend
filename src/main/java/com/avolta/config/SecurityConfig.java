package com.avolta.config;

import com.avolta.security.JwtAuthorizationFilter;
import com.avolta.security.JwtTokenProvider;
import com.avolta.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORRECTION ICI
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/publications/public/**").permitAll()
                .requestMatchers("/api/newsletter/subscribe").permitAll()
                .requestMatchers("/api/newsletter/unsubscribe").permitAll()
                .requestMatchers("/api/publications/{id}").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/auth/register").hasAuthority("SUPERADMIN")
                .requestMatchers("/api/users/**").hasAuthority("SUPERADMIN")
                .requestMatchers("/api/publications/pending").hasAuthority("SUPERADMIN")
                .requestMatchers("/api/publications/*/approve").hasAuthority("SUPERADMIN")
                .requestMatchers("/api/publications/*/reject").hasAuthority("SUPERADMIN")
                .requestMatchers("/api/publications").authenticated()
                .requestMatchers("/api/publications/**").authenticated()
                .requestMatchers("/api/newsletter/subscribers").authenticated()
                .requestMatchers("/api/newsletter/test").authenticated()
                .requestMatchers("/api/newsletter/subscribers/**").authenticated()
                .requestMatchers("/api/upload/**").authenticated()
                .requestMatchers("/api/uploads/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider, userService), UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    // ✅ NOUVELLE MÉTHODE CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Autoriser votre frontend Railway ET localhost
        configuration.addAllowedOrigin("https://avoltafrontend-production.up.railway.app");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:4173");
        
        // Autoriser toutes les méthodes HTTP
        configuration.addAllowedMethod("*");
        
        // Autoriser tous les headers
        configuration.addAllowedHeader("*");
        
        // Autoriser les credentials
        configuration.setAllowCredentials(true);
        
        // Exposer les headers d'autorisation
        configuration.addExposedHeader("Authorization");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}