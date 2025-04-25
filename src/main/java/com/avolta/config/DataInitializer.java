package com.avolta.config;

import com.avolta.models.User;
import com.avolta.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param userRepository
     * @param passwordEncoder
     */
   
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Créer un superadmin s'il n'existe pas déjà
        if (!userRepository.existsByEmail("superadmin@avolta.be")) {
            User superAdmin = new User();
            superAdmin.setEmail("superadmin@avolta.be");
            superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
            superAdmin.setRole(User.Role.SUPERADMIN);
            superAdmin.setStatus(User.Status.ACTIVE);
            superAdmin.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(superAdmin);
            System.out.println("Superadmin user created.");
        }
        
        // Créer un admin s'il n'existe pas déjà
        if (!userRepository.existsByEmail("admin@avolta.be")) {
            User admin = new User();
            admin.setEmail("admin@avolta.be");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.Status.ACTIVE);
            admin.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(admin);
            System.out.println("Admin user created.");
        }
    }
}