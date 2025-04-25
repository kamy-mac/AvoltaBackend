package com.avolta.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
    public static <T> ApiResponse<T> error(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        response.setData(data);
        return response;
    }
 
    public void setMessage(String message) {
        this.message = message;
    }
 
    public void setData(T data) {
        this.data = data;
    }
}