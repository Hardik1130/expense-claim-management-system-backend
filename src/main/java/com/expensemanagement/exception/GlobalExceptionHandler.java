package com.expensemanagement.exception;

import org.springframework.security.core.AuthenticationException;
import com.expensemanagement.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> m = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> m.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Please correct the highlighted fields.", req, m);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> duplicate(RuntimeException ex, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), r, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> missing(RuntimeException ex, HttpServletRequest r) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), r, null);
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> bad(Exception ex, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, ex instanceof HttpMessageNotReadableException ? "Invalid request body." : ex.getMessage(), r, null);
    }

    @ExceptionHandler({ForbiddenOperationException.class, AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponse> forbidden(Exception ex, HttpServletRequest r) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage() == null ? "Access denied." : ex.getMessage(), r, null);
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiErrorResponse> unauthorized(Exception ex, HttpServletRequest r) {
        return build(HttpStatus.UNAUTHORIZED, "Authentication failed.", r, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> conflict(Exception ex, HttpServletRequest r) {
        return build(HttpStatus.CONFLICT, "The request conflicts with existing data.", r, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> unexpected(Exception ex, HttpServletRequest r) {
        log.error("Unexpected error for {}", r.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", r, null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus s, String msg, HttpServletRequest r, Map<String, String> errors) {
        return ResponseEntity.status(s).body(ApiErrorResponse.builder().success(false).status(s.value()).message(msg).path(r.getRequestURI()).timestamp(LocalDateTime.now()).errors(errors).build());
    }
}
