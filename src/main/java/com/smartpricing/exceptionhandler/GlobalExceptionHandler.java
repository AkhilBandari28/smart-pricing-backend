package com.smartpricing.exceptionhandler;

import com.smartpricing.exception.BusinessException;
import com.smartpricing.exception.ResourceNotFoundException;
import com.smartpricing.util.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 BUSINESS ERRORS
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<String> handleBusiness(BusinessException ex) {
        return new ApiResponse<>(400, ex.getMessage(), null);
    }

    // 🔴 RESOURCE NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<String> handleNotFound(ResourceNotFoundException ex) {
        return new ApiResponse<>(404, ex.getMessage(), null);
    }

    // 🔴 VALIDATION ERRORS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return new ApiResponse<>(400, "Validation failed", errors);
    }

    // 🔴 INVALID PATH / ID (FIXES "undefined")
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<String> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return new ApiResponse<>(
                400,
                "Invalid parameter",
                "Parameter '" + ex.getName() + "' has invalid value"
        );
    }

    // 🔴 FALLBACK
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return new ApiResponse<>(500, "Internal Server Error", null);
    }
}
