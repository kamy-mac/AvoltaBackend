package com.avolta.controllers;

import com.avolta.dto.UserDto;
import com.avolta.dto.responses.ApiResponse;
import com.avolta.models.User;
import com.avolta.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "Only super admins can access this endpoint")
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "Get user by ID", description = "Only super admins can access this endpoint")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable String id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @Operation(summary = "Update user status", description = "Only super admins can access this endpoint")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable String id,
            @RequestParam User.Status status) {
        UserDto updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", updatedUser));
    }

    @Operation(summary = "Delete user", description = "Only super admins can access this endpoint")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(ApiResponse.success("User deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}