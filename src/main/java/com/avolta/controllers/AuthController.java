package com.avolta.controllers;

import com.avolta.dto.UserDto;
import com.avolta.dto.requests.CreateUserRequest;
import com.avolta.dto.requests.LoginRequest;
import com.avolta.dto.responses.ApiResponse;
import com.avolta.dto.responses.JwtAuthResponse;
import com.avolta.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.avolta.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "Register a new admin user", description = "Only super admins can create new admin users")
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto createdUser = userService.createUser(request);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", createdUser),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        @SuppressWarnings("unused")
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserByEmail(loginRequest.getEmail());

        JwtAuthResponse response = new JwtAuthResponse(jwt, userDto);

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    // Note: Login is handled by JwtAuthenticationFilter
}